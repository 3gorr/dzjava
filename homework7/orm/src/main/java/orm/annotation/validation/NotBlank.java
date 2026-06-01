package orm.annotation.validation;

import orm.core.validation.NotBlankValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = NotBlankValidator.class)
public @interface NotBlank {
    String message() default "must not be blank";
}
