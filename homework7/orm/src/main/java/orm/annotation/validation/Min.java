package orm.annotation.validation;

import orm.core.validation.MinValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = MinValidator.class)
public @interface Min {
    double value();
    String message() default "must be greater than or equal to the minimum";
}
