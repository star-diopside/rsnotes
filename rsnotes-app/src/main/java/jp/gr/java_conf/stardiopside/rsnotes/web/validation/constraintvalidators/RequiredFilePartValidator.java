package jp.gr.java_conf.stardiopside.rsnotes.web.validation.constraintvalidators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jp.gr.java_conf.stardiopside.rsnotes.web.validation.constraints.RequiredFilePart;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.util.StringUtils;

public class RequiredFilePartValidator implements ConstraintValidator<RequiredFilePart, FilePart> {

    @Override
    public boolean isValid(FilePart value, ConstraintValidatorContext context) {
        return value == null || StringUtils.hasLength(value.filename());
    }
}
