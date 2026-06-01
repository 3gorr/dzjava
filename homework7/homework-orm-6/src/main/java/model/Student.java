package model;

import orm.annotation.Column;
import orm.annotation.Id;
import orm.annotation.Table;

@Table(name = "students")
public class Student {

    @Id
    private Long id;

    @Column
    private String name;

    @Column
    private int age;

    @Column
    private double grade;

    public Student() {
    }

    public Student(String name, int age, double grade) {
        this.name = name;
        this.age = age;
        this.grade = grade;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public double getGrade() {
        return grade;
    }

    @Override
    public String toString() {
        return "Student{id=" + id + ", name='" + name + "', age=" + age + ", grade=" + grade + '}';
    }
}
