package jp.gr.java_conf.stardiopside.rsnotes.data.value;

import java.time.LocalDateTime;

public record FileInfoData(
        Long id,
        String fileName,
        String contentType,
        Integer length,
        String hashValue,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Integer version,
        Long fileDataId,
        LocalDateTime fileDataUpdatedAt,
        Integer fileDataVersion) {
}
