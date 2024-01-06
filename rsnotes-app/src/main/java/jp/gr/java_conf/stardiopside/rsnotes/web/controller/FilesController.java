package jp.gr.java_conf.stardiopside.rsnotes.web.controller;

import jakarta.validation.Valid;
import jp.gr.java_conf.stardiopside.rsnotes.data.entity.FileInfo;
import jp.gr.java_conf.stardiopside.rsnotes.service.FileService;
import jp.gr.java_conf.stardiopside.rsnotes.web.form.FileCreateForm;
import jp.gr.java_conf.stardiopside.rsnotes.web.form.FileEditForm;
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
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/controller/files")
public class FilesController {

    private final FileService fileService;

    public FilesController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping
    public Rendering index() {
        return Rendering.view("files/index")
                .modelAttribute("path", "/controller")
                .modelAttribute("files", fileService.list())
                .build();
    }

    @GetMapping("/{id}")
    public Mono<Rendering> show(@PathVariable("id") Long id) {
        return fileService.findFileInfo(id)
                .map(f -> Rendering.view("files/show")
                        .modelAttribute("path", "/controller")
                        .modelAttribute("fileInfo", f)
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
                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Mono.just(Rendering.view("files/create")
                    .modelAttribute("path", "/controller")
                    .build());
        }

        return fileService.save(form.getFile())
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
                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Mono.just(Rendering.view("files/edit")
                    .modelAttribute("path", "/controller")
                    .build());
        }

        return fileService.update(form.getFile(), form.toFileInfo(), form.getFileDataVersion())
                .map(f -> Rendering.redirectTo(UriComponentsBuilder
                                .fromUriString("/controller/files/{id}")
                                .build(f.getId())
                                .toString())
                        .build());
    }

    @DeleteMapping("/{id}")
    public Mono<Rendering> delete(@ModelAttribute FileInfo info) {
        if (info.getId() == null) {
            return Mono.just(Rendering.view("errors/404")
                    .status(HttpStatus.NOT_FOUND)
                    .build());
        } else {
            return fileService.delete(info)
                    .thenReturn(Rendering.redirectTo("/controller/files").build());
        }
    }
}
