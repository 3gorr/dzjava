package orm.core.validation;

import orm.annotation.validation.Max;

public final class MaxValidator implements ConstraintValidator<Max, Number> {
    @Override
    public boolean isValid(Number value, Max annotation) {
        if (value == null) {
            return true;
        }
        return value.doubleValue() <= annotation.value();
    }
}
