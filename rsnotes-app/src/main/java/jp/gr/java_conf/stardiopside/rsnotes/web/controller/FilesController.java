package jp.gr.java_conf.stardiopside.rsnotes.web.controller;

import jakarta.validation.Valid;
import jp.gr.java_conf.stardiopside.rsnotes.data.entity.FileInfo;
import jp.gr.java_conf.stardiopside.rsnotes.service.FileService;
import jp.gr.java_conf.stardiopside.rsnotes.web.form.FileCreateForm;
import jp.gr.java_conf.stardiopside.rsnotes.web.form.FileEditForm;
import jp.gr.java_conf.stardiopside.rsnotes.web.util.Constants;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.WebSession;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Controller
@RequestMapping("/controller/files")
public class FilesController {

    private final FileService fileService;
    private final MessageSource messageSource;

    public FilesController(FileService fileService, MessageSource messageSource) {
        this.fileService = fileService;
        this.messageSource = messageSource;
    }

    @GetMapping
    public Rendering index(WebSession session) {
        var messageSuccess = session.getAttributes()
                .remove(Constants.MESSAGE_KEY_SUCCESS);
        return Rendering.view("files/index")
                .modelAttribute("path", "/controller")
                .modelAttribute("files", fileService.list())
                .modelAttribute("success", messageSuccess)
                .build();
    }

    @GetMapping("/{id}")
    public Mono<Rendering> show(@PathVariable("id") Long id, WebSession session) {
        var messageSuccess = session.getAttributes()
                .remove(Constants.MESSAGE_KEY_SUCCESS);
        return fileService.findFileInfo(id)
                .map(f -> Rendering.view("files/show")
                        .modelAttribute("path", "/controller")
                        .modelAttribute("fileInfo", f)
                        .modelAttribute("success", messageSuccess)
                        .build())
                .defaultIfEmpty(Rendering.view("errors/404")
                        .status(HttpStatus.NOT_FOUND)
                        .build());
    }

    @GetMapping("/{id}/data")
    public Mono<ResponseEntity<ByteBuffer>> download(@PathVariable("id") Long id) {
        return fileService.findDownloadData(id)
                .map(d -> ResponseEntity.ok()
                        .headers(h -> h.setContentDisposition(ContentDisposition
                                .attachment()
                                .filename(d.fileName(), StandardCharsets.UTF_8)
                                .build()))
                        .contentType(StringUtils.hasLength(d.contentType())
                                ? MediaType.parseMediaType(d.contentType())
                                : MediaType.APPLICATION_OCTET_STREAM)
                        .body(d.data()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/create")
    public Rendering create() {
        return Rendering.view("files/create")
                .modelAttribute("path", "/controller")
                .modelAttribute("form", new FileCreateForm())
                .build();
    }

    @PostMapping
    public Mono<Rendering> save(@Valid @ModelAttribute("form") FileCreateForm form,
                                BindingResult bindingResult,
                                WebSession session, Locale locale) {
        if (bindingResult.hasErrors()) {
            return Mono.just(Rendering.view("files/create")
                    .modelAttribute("path", "/controller")
                    .build());
        }

        var messages = new MessageSourceAccessor(messageSource, locale);

        return fileService.save(form.getFile())
                .doOnNext(info -> session.getAttributes().put(Constants.MESSAGE_KEY_SUCCESS,
                        messages.getMessage("messages.success-create")))
                .map(f -> Rendering.redirectTo(UriComponentsBuilder
                                .fromUriString("/controller/files/{id}")
                                .build(f.getId())
                                .toString())
                        .build());
    }

    @GetMapping("/{id}/edit")
    public Mono<Rendering> edit(@PathVariable("id") Long id) {
        return fileService.findFileInfoData(id)
                .map(f -> Rendering.view("files/edit")
                        .modelAttribute("path", "/controller")
                        .modelAttribute("form", new FileEditForm(f))
                        .build())
                .defaultIfEmpty(Rendering.view("errors/404")
                        .status(HttpStatus.NOT_FOUND)
                        .build());
    }

    @PostMapping("/{id}/put")
    public Mono<Rendering> update(@Valid @ModelAttribute("form") FileEditForm form,
                                  BindingResult bindingResult,
                                  WebSession session, Locale locale) {
        if (bindingResult.hasErrors()) {
            return Mono.just(Rendering.view("files/edit")
                    .modelAttribute("path", "/controller")
                    .build());
        }

        var messages = new MessageSourceAccessor(messageSource, locale);

        return fileService.update(form.getFile(), form.toFileInfo(), form.getFileDataVersion())
                .doOnNext(info -> session.getAttributes().put(Constants.MESSAGE_KEY_SUCCESS,
                        messages.getMessage("messages.success-update")))
                .map(f -> Rendering.redirectTo(UriComponentsBuilder
                                .fromUriString("/controller/files/{id}")
                                .build(f.getId())
                                .toString())
                        .build());
    }

    @DeleteMapping("/{id}")
    public Mono<Rendering> delete(@ModelAttribute FileInfo info,
                                  WebSession session, Locale locale) {
        var messages = new MessageSourceAccessor(messageSource, locale);
        return fileService.delete(info)
                .doOnSuccess(v -> session.getAttributes().put(Constants.MESSAGE_KEY_SUCCESS,
                        messages.getMessage("messages.success-delete")))
                .thenReturn(Rendering.redirectTo("/controller/files").build());
    }
}
