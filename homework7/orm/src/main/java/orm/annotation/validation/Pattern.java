package orm.annotation.validation;

import orm.core.validation.PatternValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = PatternValidator.class)
public @interface Pattern {
    String regex();
    String message() default "does not match the required pattern";
}
