package jp.gr.java_conf.stardiopside.rsnotes.web.validation.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jp.gr.java_conf.stardiopside.rsnotes.web.validation.constraintvalidators.RequiredFilePartValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({
        ElementType.METHOD,
        ElementType.FIELD,
        ElementType.ANNOTATION_TYPE,
        ElementType.CONSTRUCTOR,
        ElementType.PARAMETER,
        ElementType.TYPE_USE
})
@Repeatable(RequiredFilePart.List.class)
@Constraint(validatedBy = {RequiredFilePartValidator.class})
public @interface RequiredFilePart {

    String message() default "{jp.gr.java_conf.stardiopside.rsnotes.web.validation.constraints.RequiredFilePart.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({
            ElementType.METHOD,
            ElementType.FIELD,
            ElementType.ANNOTATION_TYPE,
            ElementType.CONSTRUCTOR,
            ElementType.PARAMETER,
            ElementType.TYPE_USE
    })
    @interface List {
        RequiredFilePart[] value();
    }
}
