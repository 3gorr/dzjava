package orm.core.validation;

import orm.annotation.validation.Pattern;
import orm.core.OrmException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.PatternSyntaxException;

public final class PatternValidator implements ConstraintValidator<Pattern, String> {

    private final ConcurrentMap<String, java.util.regex.Pattern> cache = new ConcurrentHashMap<>();

    @Override
    public boolean isValid(String value, Pattern annotation) {
        if (value == null) {
            return true;
        }
        java.util.regex.Pattern pattern = cache.computeIfAbsent(annotation.regex(), regex -> {
            try {
                return java.util.regex.Pattern.compile(regex);
            } catch (PatternSyntaxException e) {
                throw new OrmException("Invalid regex in @Pattern: " + regex, e);
            }
        });
        return pattern.matcher(value).matches();
    }
}
