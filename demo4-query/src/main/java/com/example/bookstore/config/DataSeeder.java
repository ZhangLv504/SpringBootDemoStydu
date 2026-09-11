package com.example.bookstore.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.bookstore.model.Book;
import com.example.bookstore.repository.BookRepository;

/**
 * 种子数据：应用启动时执行（CommandLineRunner 会在 Spring 容器就绪后运行）。
 * 只插 8 本样书，方便直接体验分页查询。
 * 判断逻辑：表里还没有书才插，避免每次启动都重复。
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final BookRepository repository;

    public DataSeeder(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }
        List<Book> samples = List.of(
                new Book("Spring 实战", "Craig Walls", 108.0),
                new Book("Spring Boot 3 极简教程", "张三", 66.0),
                new Book("深入理解 Java 虚拟机", "周志明", 129.0),
                new Book("Java 并发编程实战", "Brian Goetz", 88.0),
                new Book("Effective Java", "Joshua Bloch", 79.5),
                new Book("Redis 设计与实现", "黄健宏", 59.0),
                new Book("MySQL 是怎样运行的", "小孩子4919", 95.0),
                new Book("算法导论", "Thomas H. Cormen", 199.0)
        );
        repository.saveAll(samples);
        System.out.println("[DataSeeder] 已插入 " + samples.size() + " 本样书用于查询/分页演示");
    }
}