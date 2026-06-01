package orm.annotation.validation;

import orm.core.validation.MaxValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = MaxValidator.class)
public @interface Max {
    double value();
    String message() default "must be less than or equal to the maximum";
}
