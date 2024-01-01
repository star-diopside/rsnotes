package jp.gr.java_conf.stardiopside.rsnotes.web.form;

import lombok.Data;
import org.springframework.http.codec.multipart.FilePart;

@Data
public class FileForm {

    private FilePart file;

}
