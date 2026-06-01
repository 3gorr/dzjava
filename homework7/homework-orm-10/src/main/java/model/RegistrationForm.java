package model;

import orm.annotation.Column;
import orm.annotation.Id;
import orm.annotation.Table;
import orm.annotation.validation.Max;
import orm.annotation.validation.Min;
import orm.annotation.validation.NotBlank;
import orm.annotation.validation.NotNull;
import orm.annotation.validation.Size;

@Table(name = "registration_forms")
public class RegistrationForm {

    @Id
    private Long id;

    @Column
    @NotBlank
    @Size(min = 3, max = 30)
    private String login;

    @Column
    @NotNull
    @Size(min = 8, max = 100)
    private String password;

    @Column
    @Min(18)
    @Max(120)
    private int age;

    public RegistrationForm() {
    }

    public RegistrationForm(String login, String password, int age) {
        this.login = login;
        this.password = password;
        this.age = age;
    }

    public Long getId() { return id; }
    public String getLogin() { return login; }
    public String getPassword() { return password; }
    public int getAge() { return age; }

    @Override
    public String toString() {
        return "RegistrationForm{id=" + id + ", login='" + login + "', password='"
                + (password == null ? null : "***") + "', age=" + age + '}';
    }
}
