package com.example.bookstore.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.bookstore.dto.BookRequest;
import com.example.bookstore.model.Book;
import com.example.bookstore.repository.BookRepository;

/**
 * Service 层：业务逻辑。
 * 相比 Demo1，这里删掉了手写的 Map 和 AtomicLong，
 * 一切读写都委托给 BookRepository（真正落库到 H2）。
 * Service 对上层 Controller 提供的接口签名完全不变 —— 这就是解耦的好处。
 */
@Service
public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public List<Book> findAll() {
        return repository.findAll();
    }

    public Optional<Book> findById(Long id) {
        return repository.findById(id);
    }

    public Book create(BookRequest request) {
        Book book = new Book(request.title(), request.author(), request.price());
        // save() 有记录则更新、没有则插入；id 由数据库自增生成
        return repository.save(book);
    }
}