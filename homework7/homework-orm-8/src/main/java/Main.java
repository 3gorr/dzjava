import model.Student;
import orm.repository.DBConfig;
import orm.repository.EntityManager;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class Main {

    public static void main(String[] args) throws Exception {
        try (Connection connection = DBConfig.getConnection()) {
            EntityManager em = new EntityManager(connection);
            em.createTable(Student.class);

            Long id1 = em.save(new Student("Иван Иванов", 20, 4.5, LocalDate.of(2023, 9, 1)));
            Long id2 = em.save(new Student("Пётр Петров", 21, 4.8, LocalDate.of(2022, 9, 1)));
            Long id3 = em.save(new Student("Анна Смирнова", 19, 5.0, LocalDate.of(2024, 9, 1)));
            System.out.println("Saved ids: " + id1 + ", " + id2 + ", " + id3);

            System.out.println("\n[findById(" + id2 + ")]");
            Optional<Student> found = em.findById(Student.class, id2);
            found.ifPresent(System.out::println);

            System.out.println("\n[findAll]");
            List<Student> all = em.findAll(Student.class);
            all.forEach(System.out::println);

            System.out.println("\n[findById(9999) — missing]");
            Optional<Student> missing = em.findById(Student.class, 9999L);
            System.out.println("present? " + missing.isPresent());
        }
    }
}
