package orm.core.validation;

import orm.annotation.validation.Size;

public final class SizeValidator implements ConstraintValidator<Size, String> {
    @Override
    public boolean isValid(String value, Size annotation) {
        if (value == null) {
            return true;
        }
        int length = value.length();
        return length >= annotation.min() && length <= annotation.max();
    }
}
