package orm.core.validation;

import orm.annotation.validation.Min;

public final class MinValidator implements ConstraintValidator<Min, Number> {
    @Override
    public boolean isValid(Number value, Min annotation) {
        if (value == null) {
            return true;
        }
        return value.doubleValue() >= annotation.value();
    }
}
