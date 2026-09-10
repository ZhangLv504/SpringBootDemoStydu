package com.example.bookstore.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA 实体：Book 类现在与数据库表 books 一一对应。
 * - @Entity       告诉 Hibernate 这个类是要持久化(建表)的实体
 * - @Table(name)  指定对应表名（省略则默认用类名 books）
 * - @Id + @GeneratedValue  主键，且由数据库自增生成（IDENTITY）
 *
 * 注意：JPA 实体要求必须有【无参构造器】，并且字段要有 getter/setter。
 * 这跟 Demo1 里那个手写 id 的 Book 不同——主键交给数据库管了。
 */
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String author;

    private Double price;

    public Book() {
    }

    public Book(String title, String author, Double price) {
        this.title = title;
        this.author = author;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}