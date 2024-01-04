package jp.gr.java_conf.stardiopside.rsnotes.data.repository;

import jp.gr.java_conf.stardiopside.rsnotes.data.entity.FileInfo;
import jp.gr.java_conf.stardiopside.rsnotes.data.value.DownloadData;
import jp.gr.java_conf.stardiopside.rsnotes.data.value.FileInfoData;
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

    @Query("""
            SELECT
              i.id,
              i.file_name,
              i.content_type,
              i.length,
              i.hash_value,
              i.created_at,
              i.updated_at,
              i.version,
              d.id AS file_data_id,
              d.updated_at AS file_data_updated_at,
              d.version AS file_data_version
            FROM
              file_info i
              INNER JOIN file_data d
              ON i.id = d.file_info_id
            WHERE
              i.id = :id
            """)
    Mono<FileInfoData> findFileInfoDataById(Long id);

}
