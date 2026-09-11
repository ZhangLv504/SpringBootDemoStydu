package com.example.bookstore.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.example.bookstore.dto.BookRequest;
import com.example.bookstore.model.Book;
import com.example.bookstore.service.BookService;

/**
 * Controller 层：新增了查询类端点。
 * - /api/books?keyword=Spring    按标题模糊搜（派生查询）
 * - /api/books/by-author?author= 按作者精确搜
 * - /api/books/search?kw=        标题或作者多字段搜(@Query)
 * - /api/books/paged?page=0&size=3  分页查询
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<Book> list(@RequestParam(required = false) String keyword) {
        // 没传 keyword 就查全部，传了就按标题模糊搜
        return keyword == null || keyword.isBlank()
                ? bookService.findAll()
                : bookService.findByKeyword(keyword);
    }

    @GetMapping("/by-author")
    public List<Book> byAuthor(@RequestParam String author) {
        return bookService.findByAuthor(author);
    }

    @GetMapping("/search")
    public List<Book> search(@RequestParam String kw) {
        return bookService.search(kw);
    }

    @GetMapping("/paged")
    public Page<Book> paged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookService.findPage(pageable);
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