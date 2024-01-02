package jp.gr.java_conf.stardiopside.rsnotes.web.form;

import jakarta.validation.constraints.NotNull;
import jp.gr.java_conf.stardiopside.rsnotes.web.validation.constraints.RequiredFilePart;
import lombok.Data;
import org.springframework.http.codec.multipart.FilePart;

@Data
public class FileForm {

    @NotNull
    @RequiredFilePart
    private FilePart file;

}
