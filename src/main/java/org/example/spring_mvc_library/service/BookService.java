package org.example.spring_mvc_library.service;

import org.example.spring_mvc_library.models.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {

    //Page<Book> getAllBooks(int page, int size, String sort);

    Page<Book> getAllBooks(Pageable pageable);
    Book getBookById(Long id);
    Book createBook(Book book);
    Book updateBook(Long id, Book book);
    void deleteBook(Long id);
}
