package jp.gr.java_conf.stardiopside.rsnotes.web.handler;

import jp.gr.java_conf.stardiopside.rsnotes.service.FileService;
import jp.gr.java_conf.stardiopside.rsnotes.web.form.FileCreateForm;
import jp.gr.java_conf.stardiopside.rsnotes.web.form.FileEditForm;
import jp.gr.java_conf.stardiopside.rsnotes.web.util.RequestPathParser;
import jp.gr.java_conf.stardiopside.rsnotes.web.util.WebExchangeDataBindings;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class FilesHandler {

    private final FileService fileService;
    private final WebExchangeDataBindings webExchangeDataBindings;

    public FilesHandler(FileService fileService, WebExchangeDataBindings webExchangeDataBindings) {
        this.fileService = fileService;
        this.webExchangeDataBindings = webExchangeDataBindings;
    }

    public Mono<ServerResponse> index(ServerRequest request) {
        return ServerResponse.ok().render("files/index",
                Map.of("files", fileService.list()));
    }

    public Mono<ServerResponse> show(ServerRequest request) {
        return RequestPathParser.parseId(request)
                .flatMap(fileService::findFileInfo)
                .flatMap(f -> ServerResponse.ok().render("files/show", f))
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
        return webExchangeDataBindings.bindAndValidate(request, new FileCreateForm())
                .flatMap(r -> {
                    var form = r.target();
                    var bindingResult = r.bindingResult();
                    if (bindingResult.hasErrors()) {
                        return ServerResponse.ok().render("files/create",
                                Map.of("form", form,
                                        BindingResult.MODEL_KEY_PREFIX + "form", bindingResult));
                    }

                    return fileService.save(form.getFile())
                            .flatMap(f -> ServerResponse.seeOther(UriComponentsBuilder
                                    .fromUriString("/files/{id}").build(f.getId())).build());
                });
    }

    public Mono<ServerResponse> edit(ServerRequest request) {
        return RequestPathParser.parseId(request)
                .flatMap(fileService::findFileInfoData)
                .flatMap(f -> ServerResponse.ok().render("files/edit",
                        Map.of("form", new FileEditForm(f))));
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

                    return fileService.update(form.getFile(), form.toFileInfo(), form.getFileDataVersion())
                            .flatMap(f -> ServerResponse.seeOther(UriComponentsBuilder
                                    .fromUriString("/files/{id}").build(f.getId())).build());
                });
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        throw new UnsupportedOperationException();
    }
}
