import model.Book;
import orm.core.OrmException;
import orm.repository.DBConfig;
import orm.repository.EntityManager;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Main {

    public static void main(String[] args) throws Exception {
        try (Connection connection = DBConfig.getConnection()) {
            EntityManager em = new EntityManager(connection);
            em.createTable(Book.class);

            System.out.println("[saveAll: 5 books]");
            List<Book> books = Arrays.asList(
                    new Book("Война и мир", "Лев Толстой", 1869, true),
                    new Book("Анна Каренина", "Лев Толстой", 1877, true),
                    new Book("Преступление и наказание", "Фёдор Достоевский", 1866, false),
                    new Book("Идиот", "Фёдор Достоевский", 1869, true),
                    new Book("Мёртвые души", "Николай Гоголь", 1842, true)
            );
            em.saveAll(books);
            books.forEach(b -> System.out.println("  saved id=" + b.getId() + " -> " + b.getTitle()));

            System.out.println("\n[findAllWhere: author = Лев Толстой]");
            List<Book> tolstoy = em.findAllWhere(Book.class, "author", "Лев Толстой");
            tolstoy.forEach(System.out::println);

            System.out.println("\n[update: 'Идиот' -> available=false]");
            Book idiot = em.findOneWhere(Book.class, "title", "Идиот").orElseThrow();
            idiot.setAvailable(false);
            int updated = em.update(idiot);
            System.out.println("rows updated: " + updated);
            System.out.println("now: " + em.findById(Book.class, idiot.getId()).orElseThrow());

            System.out.println("\n[findOneWhere: title = 'Мёртвые души']");
            Optional<Book> one = em.findOneWhere(Book.class, "title", "Мёртвые души");
            one.ifPresent(System.out::println);

            System.out.println("\n[delete: первая книга]");
            Book first = books.get(0);
            int deleted = em.delete(first);
            System.out.println("rows deleted: " + deleted);

            System.out.println("\n[count]");
            System.out.println("count = " + em.count(Book.class));

            System.out.println("\n[saveAll rollback: одна книга с title=null]");
            List<Book> bad = Arrays.asList(
                    new Book("Новая книга 1", "Автор 1", 2020, true),
                    new Book(null, "Автор 2", 2021, true),
                    new Book("Новая книга 3", "Автор 3", 2022, true)
            );
            long countBefore = em.count(Book.class);
            try {
                em.saveAll(bad);
            } catch (OrmException e) {
                System.out.println("ожидаемое исключение: " + e.getMessage());
            }
            long countAfter = em.count(Book.class);
            System.out.println("count до: " + countBefore + ", после: " + countAfter
                    + " (одинаковы — транзакция откатилась)");
        }
    }
}
