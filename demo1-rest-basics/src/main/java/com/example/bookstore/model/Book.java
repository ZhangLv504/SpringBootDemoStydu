package com.example.bookstore.model;

/**
 * Model 层：数据模型，只负责持有一个"图书"的数据字段。
 * 它不关心数据从哪来、也不关心怎么传给前端。
 */
public class Book {

    private Long id;
    private String title;
    private String author;
    private Double price;

    public Book() {
    }

    public Book(Long id, String title, String author, Double price) {
        this.id = id;
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