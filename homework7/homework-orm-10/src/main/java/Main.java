import model.RegistrationForm;
import orm.core.validation.ValidationException;
import orm.repository.DBConfig;
import orm.repository.EntityManager;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        try (Connection connection = DBConfig.getConnection()) {
            EntityManager em = new EntityManager(connection);
            em.createTable(RegistrationForm.class);

            System.out.println("[1] Сохранение валидной формы");
            RegistrationForm ok = new RegistrationForm("egor", "verystrongpwd", 25);
            Long id = em.save(ok);
            System.out.println("  saved id=" + id + " -> " + ok);

            System.out.println("\n[2] Попытка сохранить НЕвалидную форму (все нарушения сразу)");
            RegistrationForm bad = new RegistrationForm("ab", null, 5);
            try {
                em.save(bad);
            } catch (ValidationException e) {
                System.out.println("  поймали ValidationException, нарушений: " + e.violations().size());
                e.violations().forEach(v ->
                        System.out.println("    - field=" + v.field()
                                + ", value=" + v.invalidValue()
                                + ", annotation=@" + v.annotation().getSimpleName()
                                + ", message=" + v.message()));
            }

            System.out.println("\n[3] Откат saveAll, если в пачке есть невалидная форма");
            long countBefore = em.count(RegistrationForm.class);
            List<RegistrationForm> batch = Arrays.asList(
                    new RegistrationForm("alice", "strongpass1", 22),
                    new RegistrationForm("bo", "x", 200), // три нарушения
                    new RegistrationForm("charlie", "strongpass2", 30)
            );
            try {
                em.saveAll(batch);
            } catch (ValidationException e) {
                System.out.println("  поймали ValidationException, нарушений: " + e.violations().size());
                e.violations().forEach(v ->
                        System.out.println("    - field=" + v.field()
                                + ", value=" + v.invalidValue()
                                + ", annotation=@" + v.annotation().getSimpleName()));
            }
            long countAfter = em.count(RegistrationForm.class);
            System.out.println("  count до: " + countBefore + ", после: " + countAfter
                    + " (одинаковы — ни одна запись не попала в БД)");
        }
    }
}
