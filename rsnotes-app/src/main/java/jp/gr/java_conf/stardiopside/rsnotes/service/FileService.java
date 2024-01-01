package jp.gr.java_conf.stardiopside.rsnotes.service;

import jp.gr.java_conf.stardiopside.rsnotes.data.entity.FileInfo;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public interface FileService {

    Mono<FileInfo> find(Long id);

    Mono<FileInfo> save(FilePart filePart);

}
