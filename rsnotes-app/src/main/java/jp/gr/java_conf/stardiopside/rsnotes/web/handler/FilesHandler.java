package jp.gr.java_conf.stardiopside.rsnotes.web.handler;

import jp.gr.java_conf.stardiopside.rsnotes.service.FileService;
import jp.gr.java_conf.stardiopside.rsnotes.web.form.FileForm;
import jp.gr.java_conf.stardiopside.rsnotes.web.util.RequestPathParser;
import jp.gr.java_conf.stardiopside.rsnotes.web.util.WebExchangeDataBindings;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

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
        throw new UnsupportedOperationException();
    }

    public Mono<ServerResponse> show(ServerRequest request) {
        return RequestPathParser.parseId(request)
                .flatMap(fileService::find)
                .flatMap(f -> ServerResponse.ok().contentType(MediaType.TEXT_HTML)
                        .render("files/show", f))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        return ServerResponse.ok().contentType(MediaType.TEXT_HTML)
                .render("files/create", Map.of("form", new FileForm()));
    }

    public Mono<ServerResponse> save(ServerRequest request) {
        return webExchangeDataBindings.bindAndValidate(request, new FileForm())
                .flatMap(tuple -> {
                    var form = tuple.getT1();
                    var bindingResult = tuple.getT2();
                    if (bindingResult.hasErrors()) {
                        return ServerResponse.ok().contentType(MediaType.TEXT_HTML)
                                .render("files/create", Map.of("form", form));
                    }

                    return fileService.save(form.getFile())
                            .flatMap(f -> ServerResponse.seeOther(UriComponentsBuilder
                                    .fromUriString("/files/{id}").build(f.getId())).build());
                });
    }

    public Mono<ServerResponse> edit(ServerRequest request) {
        throw new UnsupportedOperationException();
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        throw new UnsupportedOperationException();
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        throw new UnsupportedOperationException();
    }
}
