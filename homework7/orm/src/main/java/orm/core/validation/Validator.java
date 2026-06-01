package orm.core.validation;

import orm.annotation.validation.Constraint;
import orm.core.OrmException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class Validator {

    private final ConcurrentMap<Class<? extends ConstraintValidator<?, ?>>, ConstraintValidator<?, ?>> instances =
            new ConcurrentHashMap<>();

    private final ConcurrentMap<Class<? extends ConstraintValidator<?, ?>>, Class<?>> supportedTypes =
            new ConcurrentHashMap<>();

    private final ConcurrentMap<Class<?>, List<FieldValidation>> perClass = new ConcurrentHashMap<>();

    private static final Map<Class<?>, Class<?>> BOXED = new HashMap<>();

    static {
        BOXED.put(int.class, Integer.class);
        BOXED.put(long.class, Long.class);
        BOXED.put(double.class, Double.class);
        BOXED.put(float.class, Float.class);
        BOXED.put(short.class, Short.class);
        BOXED.put(byte.class, Byte.class);
        BOXED.put(boolean.class, Boolean.class);
        BOXED.put(char.class, Character.class);
    }

    public List<Violation> validate(Object entity) {
        if (entity == null) {
            throw new OrmException("Cannot validate null entity");
        }
        List<FieldValidation> validations = perClass.computeIfAbsent(entity.getClass(), this::buildValidations);
        List<Violation> violations = new ArrayList<>();
        for (FieldValidation fv : validations) {
            Object value;
            try {
                value = fv.field.get(entity);
            } catch (IllegalAccessException e) {
                throw new OrmException("Failed to read field " + fv.field.getName(), e);
            }
            for (AnnotationValidator av : fv.validators) {
                if (!invoke(av.validator, value, av.annotation)) {
                    violations.add(new Violation(
                            fv.field.getName(),
                            value,
                            readMessage(av.annotation),
                            av.annotation.annotationType()
                    ));
                }
            }
        }
        return violations;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private boolean invoke(ConstraintValidator validator, Object value, Annotation annotation) {
        return validator.isValid(value, annotation);
    }

    private List<FieldValidation> buildValidations(Class<?> clazz) {
        List<FieldValidation> result = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            List<AnnotationValidator> annValidators = new ArrayList<>();
            for (Annotation annotation : field.getAnnotations()) {
                Constraint constraint = annotation.annotationType().getAnnotation(Constraint.class);
                if (constraint == null) {
                    continue;
                }
                Class<? extends ConstraintValidator<?, ?>> validatorClass = constraint.validatedBy();
                ConstraintValidator<?, ?> validator = getOrCreate(validatorClass);
                Class<?> supportedType = supportedTypes.get(validatorClass);
                checkTypeCompatibility(field, annotation, supportedType);
                annValidators.add(new AnnotationValidator(annotation, validator));
            }
            if (!annValidators.isEmpty()) {
                field.setAccessible(true);
                result.add(new FieldValidation(field, annValidators));
            }
        }
        return result;
    }

    private ConstraintValidator<?, ?> getOrCreate(Class<? extends ConstraintValidator<?, ?>> validatorClass) {
        return instances.computeIfAbsent(validatorClass, cls -> {
            try {
                ConstraintValidator<?, ?> v = cls.getDeclaredConstructor().newInstance();
                supportedTypes.put(cls, resolveSupportedType(cls));
                return v;
            } catch (ReflectiveOperationException e) {
                throw new OrmException("Failed to instantiate validator " + cls.getName(), e);
            }
        });
    }

    private Class<?> resolveSupportedType(Class<?> validatorClass) {
        for (Type iface : validatorClass.getGenericInterfaces()) {
            if (iface instanceof ParameterizedType pt
                    && pt.getRawType() == ConstraintValidator.class) {
                Type t = pt.getActualTypeArguments()[1];
                if (t instanceof Class<?> c) {
                    return c;
                }
                if (t instanceof ParameterizedType ptt && ptt.getRawType() instanceof Class<?> c) {
                    return c;
                }
            }
        }
        throw new OrmException("Cannot resolve supported type of " + validatorClass.getName());
    }

    private void checkTypeCompatibility(Field field, Annotation annotation, Class<?> supportedType) {
        Class<?> fieldType = field.getType();
        Class<?> effective = fieldType.isPrimitive() ? BOXED.get(fieldType) : fieldType;
        if (!supportedType.isAssignableFrom(effective)) {
            throw new OrmException("Annotation @" + annotation.annotationType().getSimpleName()
                    + " on field '" + field.getName() + "' is not applicable to type "
                    + fieldType.getName() + " (validator supports " + supportedType.getName() + ")");
        }
    }

    private String readMessage(Annotation annotation) {
        try {
            Method m = annotation.annotationType().getDeclaredMethod("message");
            return (String) m.invoke(annotation);
        } catch (ReflectiveOperationException e) {
            return "validation failed";
        }
    }

    private record AnnotationValidator(Annotation annotation, ConstraintValidator<?, ?> validator) {}

    private record FieldValidation(Field field, List<AnnotationValidator> validators) {}
}
