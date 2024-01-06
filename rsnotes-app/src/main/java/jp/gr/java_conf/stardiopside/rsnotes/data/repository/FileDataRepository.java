package jp.gr.java_conf.stardiopside.rsnotes.data.repository;

import jp.gr.java_conf.stardiopside.rsnotes.data.entity.FileData;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface FileDataRepository extends R2dbcRepository<FileData, Long> {

    <T> Mono<T> findByFileInfoId(Long fileInfoId, Class<T> type);

}
