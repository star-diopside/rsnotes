package jp.gr.java_conf.stardiopside.rsnotes.web.handler;

import jp.gr.java_conf.stardiopside.rsnotes.data.entity.FileInfo;
import jp.gr.java_conf.stardiopside.rsnotes.service.FileService;
import jp.gr.java_conf.stardiopside.rsnotes.web.form.FileCreateForm;
import jp.gr.java_conf.stardiopside.rsnotes.web.form.FileEditForm;
import jp.gr.java_conf.stardiopside.rsnotes.web.util.Constants;
import jp.gr.java_conf.stardiopside.rsnotes.web.util.RequestPathParser;
import jp.gr.java_conf.stardiopside.rsnotes.web.util.WebExchangeDataBindings;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

@Component
public class FilesHandler {

    private final FileService fileService;
    private final WebExchangeDataBindings webExchangeDataBindings;
    private final MessageSource messageSource;

    public FilesHandler(FileService fileService, WebExchangeDataBindings webExchangeDataBindings,
                        MessageSource messageSource) {
        this.fileService = fileService;
        this.webExchangeDataBindings = webExchangeDataBindings;
        this.messageSource = messageSource;
    }

    public Mono<ServerResponse> index(ServerRequest request) {
        var messageSuccess = request.session()
                .flatMap(session -> Mono.justOrEmpty(session.getAttributes()
                        .remove(Constants.MESSAGE_KEY_SUCCESS)));
        return ServerResponse.ok().render("files/index",
                Map.of("files", fileService.list(),
                        "success", messageSuccess));
    }

    public Mono<ServerResponse> show(ServerRequest request) {
        var messageSuccess = request.session()
                .flatMap(session -> Mono.justOrEmpty(session.getAttributes()
                        .remove(Constants.MESSAGE_KEY_SUCCESS)));
        return RequestPathParser.parseId(request)
                .flatMap(fileService::findFileInfo)
                .flatMap(f -> ServerResponse.ok().render("files/show",
                        Map.of("fileInfo", f,
                                "success", messageSuccess)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> download(ServerRequest request) {
        return RequestPathParser.parseId(request)
                .flatMap(fileService::findDownloadData)
                .flatMap(d -> ServerResponse.ok()
                        .headers(h -> h.setContentDisposition(ContentDisposition
                                .attachment()
                                .filename(d.fileName(), StandardCharsets.UTF_8)
                                .build()))
                        .contentType(StringUtils.hasLength(d.contentType())
                                ? MediaType.parseMediaType(d.contentType())
                                : MediaType.APPLICATION_OCTET_STREAM)
                        .bodyValue(d.data()))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        return ServerResponse.ok().render("files/create",
                Map.of("form", new FileCreateForm()));
    }

    public Mono<ServerResponse> save(ServerRequest request) {
        return webExchangeDataBindings.bindAndValidate(request, FileCreateForm.class)
                .flatMap(r -> {
                    var form = r.target();
                    var bindingResult = r.bindingResult();
                    if (bindingResult.hasErrors()) {
                        return ServerResponse.ok().render("files/create",
                                Map.of("form", form,
                                        BindingResult.MODEL_KEY_PREFIX + "form", bindingResult));
                    }

                    var messages = new MessageSourceAccessor(messageSource,
                            request.exchange().getLocaleContext().getLocale());

                    return fileService.save(form.getFile())
                            .doOnNext(info -> request.session().subscribe(session ->
                                    session.getAttributes().put(Constants.MESSAGE_KEY_SUCCESS,
                                            messages.getMessage("messages.success-create"))))
                            .flatMap(f -> ServerResponse.seeOther(UriComponentsBuilder
                                    .fromUriString("/files/{id}").build(f.getId())).build());
                });
    }

    public Mono<ServerResponse> edit(ServerRequest request) {
        return RequestPathParser.parseId(request)
                .flatMap(fileService::findFileInfoData)
                .flatMap(f -> ServerResponse.ok().render("files/edit",
                        Map.of("form", new FileEditForm(f))))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        return RequestPathParser.parseId(request)
                .flatMap(id -> webExchangeDataBindings.bindAndValidate(request, new FileEditForm(id)))
                .flatMap(r -> {
                    var form = r.target();
                    var bindingResult = r.bindingResult();
                    if (bindingResult.hasErrors()) {
                        return ServerResponse.ok().render("files/edit",
                                Map.of("form", form,
                                        BindingResult.MODEL_KEY_PREFIX + "form", bindingResult));
                    }

                    var messages = new MessageSourceAccessor(messageSource,
                            request.exchange().getLocaleContext().getLocale());

                    return fileService.update(form.getFile(), form.toFileInfo(), form.getFileDataVersion())
                            .doOnNext(info -> request.session().subscribe(session ->
                                    session.getAttributes().put(Constants.MESSAGE_KEY_SUCCESS,
                                            messages.getMessage("messages.success-update"))))
                            .flatMap(f -> ServerResponse.seeOther(UriComponentsBuilder
                                    .fromUriString("/files/{id}").build(f.getId())).build());
                })
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        var messages = new MessageSourceAccessor(messageSource,
                request.exchange().getLocaleContext().getLocale());
        return RequestPathParser.parseId(request)
                .flatMap(id -> webExchangeDataBindings.bind(request, FileInfo.builder().id(id).build()))
                .map(Optional::of).defaultIfEmpty(Optional.empty())
                .flatMap(o -> o
                        .map(r -> r.getOrError()
                                .flatMap(fileService::delete)
                                .doOnSuccess(v -> request.session().subscribe(session ->
                                        session.getAttributes().put(Constants.MESSAGE_KEY_SUCCESS,
                                                messages.getMessage("messages.success-delete"))))
                                .then(ServerResponse.seeOther(URI.create("/files")).build()))
                        .orElse(ServerResponse.notFound().build()));
    }
}
