package com.example.bookstore.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.example.bookstore.dto.BookRequest;
import com.example.bookstore.model.Book;
import com.example.bookstore.service.BookService;

/**
 * Controller 层：只做 HTTP 交互，业务交给 Service。
 * - 入参加 @Valid：触发 BookRequest 上的校验注解，不合格自动抛 400
 * - getById 不再自己拼 404，交给 BookService 抛异常、全局处理器统一兜底
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<Book> list() {
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    public Book getById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Book> create(@Valid @RequestBody BookRequest request) {
        Book created = bookService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}