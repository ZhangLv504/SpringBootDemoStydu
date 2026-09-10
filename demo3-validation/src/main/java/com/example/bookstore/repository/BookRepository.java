package com.example.bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bookstore.model.Book;

/**
 * Repository 层：Spring Data JPA 的核心魔力所在。
 *
 * 只要继承 JpaRepository<实体类型, 主键类型>，Spring Data 会在运行时
 * 【自动生成实现类】，直接给你现成的增删改查方法：
 *   findAll() / findById(id) / save(...) / deleteById(id) ...
 *
 * 你也可以【按方法名声明】自定义查询，例如：
 *   List<Book> findByTitle(String title);
 * 只要方法名符合命名规范，Spring Data 就能自动翻译成 SQL，无需写实现。
 * （Demo 2 先用内置方法，自定义查询放到后面的 Demo。）
 */
public interface BookRepository extends JpaRepository<Book, Long> {
}