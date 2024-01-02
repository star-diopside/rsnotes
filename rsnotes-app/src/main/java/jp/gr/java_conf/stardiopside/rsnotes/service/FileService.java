package jp.gr.java_conf.stardiopside.rsnotes.service;

import jp.gr.java_conf.stardiopside.rsnotes.data.entity.FileInfo;
import jp.gr.java_conf.stardiopside.rsnotes.data.value.DownloadData;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FileService {

    Flux<FileInfo> list();

    Mono<FileInfo> findFileInfo(Long id);

    Mono<DownloadData> findDownloadData(Long id);

    Mono<FileInfo> save(FilePart filePart);

}
