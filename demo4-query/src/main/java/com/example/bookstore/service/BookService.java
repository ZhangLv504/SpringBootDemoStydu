package com.example.bookstore.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.bookstore.dto.BookRequest;
import com.example.bookstore.exception.BookNotFoundException;
import com.example.bookstore.model.Book;
import com.example.bookstore.repository.BookRepository;

/**
 * Service 层：只定义"能提供什么能力"，具体 SQL 交给 Repository。
 * 方法签名对上层稳定，即便底层改成 MySQL 也不影响。
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

    public Book findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    public Book create(BookRequest request) {
        Book book = new Book(request.title(), request.author(), request.price());
        return repository.save(book);
    }

    public List<Book> findByKeyword(String keyword) {
        return repository.findByTitleContainingIgnoreCase(keyword);
    }

    public List<Book> findByAuthor(String author) {
        return repository.findByAuthor(author);
    }

    public List<Book> search(String kw) {
        return repository.search(kw);
    }

    public Page<Book> findPage(Pageable pageable) {
        return repository.findAll(pageable);
    }
}