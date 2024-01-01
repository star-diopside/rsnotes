package jp.gr.java_conf.stardiopside.rsnotes.data.repository;

import jp.gr.java_conf.stardiopside.rsnotes.data.entity.FileData;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface FileDataRepository extends R2dbcRepository<FileData, Long> {
}
