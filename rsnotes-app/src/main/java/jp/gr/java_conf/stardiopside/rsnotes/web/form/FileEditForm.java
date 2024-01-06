package jp.gr.java_conf.stardiopside.rsnotes.web.form;

import jp.gr.java_conf.stardiopside.rsnotes.data.entity.FileInfo;
import jp.gr.java_conf.stardiopside.rsnotes.data.value.FileInfoData;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.codec.multipart.FilePart;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class FileEditForm {

    private FilePart file;

    private Long id;

    private String fileName;

    private String updateFileName;

    private String contentType;

    private Integer length;

    private String hashValue;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime updatedAt;

    private Integer fileInfoVersion;

    private Integer fileDataVersion;

    public FileEditForm(Long id) {
        this.id = id;
    }

    public FileEditForm(FileInfoData file) {
        id = file.id();
        fileName = file.fileName();
        contentType = file.contentType();
        length = file.length();
        hashValue = file.hashValue();
        createdAt = file.createdAt();
        updatedAt = ObjectUtils.max(file.updatedAt(), file.fileDataUpdatedAt());
        fileInfoVersion = file.version();
        fileDataVersion = file.fileDataVersion();
    }

    public FileInfo toFileInfo() {
        return FileInfo.builder()
                .id(id)
                .fileName(updateFileName)
                .contentType(contentType)
                .length(length)
                .hashValue(hashValue)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .version(fileInfoVersion)
                .build();
    }
}
