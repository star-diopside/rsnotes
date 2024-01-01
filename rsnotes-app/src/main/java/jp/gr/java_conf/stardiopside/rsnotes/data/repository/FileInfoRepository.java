package jp.gr.java_conf.stardiopside.rsnotes.data.repository;

import jp.gr.java_conf.stardiopside.rsnotes.data.entity.FileInfo;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface FileInfoRepository extends R2dbcRepository<FileInfo, Long> {
}
