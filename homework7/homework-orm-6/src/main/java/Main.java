import model.Student;
import orm.repository.DBConfig;
import orm.repository.EntityManager;

import java.sql.Connection;

public class Main {

    public static void main(String[] args) throws Exception {
        try (Connection connection = DBConfig.getConnection()) {
            EntityManager em = new EntityManager(connection);
            em.createTable(Student.class);

            Student s1 = new Student("Иван Иванов", 20, 4.5);
            Student s2 = new Student("Пётр Петров", 21, 4.8);
            Student s3 = new Student("Анна Смирнова", 19, 5.0);

            Long id1 = em.save(s1);
            Long id2 = em.save(s2);
            Long id3 = em.save(s3);

            System.out.println("Generated ids:");
            System.out.println("  s1.id = " + id1 + " (field: " + s1.getId() + ")");
            System.out.println("  s2.id = " + id2 + " (field: " + s2.getId() + ")");
            System.out.println("  s3.id = " + id3 + " (field: " + s3.getId() + ")");
        }
    }
}
