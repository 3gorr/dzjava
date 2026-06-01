package orm.annotation.validation;

import orm.core.validation.NotNullValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = NotNullValidator.class)
public @interface NotNull {
    String message() default "must not be null";
}
