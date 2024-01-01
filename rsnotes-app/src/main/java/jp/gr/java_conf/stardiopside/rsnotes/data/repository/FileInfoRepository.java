package jp.gr.java_conf.stardiopside.rsnotes.data.repository;

import jp.gr.java_conf.stardiopside.rsnotes.data.entity.FileInfo;
import jp.gr.java_conf.stardiopside.rsnotes.data.value.DownloadData;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface FileInfoRepository extends R2dbcRepository<FileInfo, Long> {

    @Query("""
            SELECT
              i.file_name,
              i.content_type,
              d.data
            FROM
              file_info i
              INNER JOIN file_data d
              ON i.id = d.file_info_id
            WHERE
              i.id = :id
            """)
    Mono<DownloadData> findDownloadDataById(Long id);

}
