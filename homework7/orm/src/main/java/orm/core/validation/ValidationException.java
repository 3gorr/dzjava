package orm.core.validation;

import orm.core.OrmException;

import java.util.List;
import java.util.stream.Collectors;

public class ValidationException extends OrmException {

    private final List<Violation> violations;

    public ValidationException(List<Violation> violations) {
        super(buildMessage(violations));
        this.violations = List.copyOf(violations);
    }

    public List<Violation> violations() {
        return violations;
    }

    private static String buildMessage(List<Violation> violations) {
        return "Validation failed with " + violations.size() + " violation(s): "
                + violations.stream()
                .map(v -> v.field() + " " + v.message())
                .collect(Collectors.joining("; "));
    }
}
