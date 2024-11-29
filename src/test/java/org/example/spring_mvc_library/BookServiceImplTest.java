package org.example.spring_mvc_library;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.persistence.EntityNotFoundException;
import org.example.spring_mvc_library.models.Author;
import org.example.spring_mvc_library.models.Book;
import org.example.spring_mvc_library.repository.BookRepository;
import org.example.spring_mvc_library.service.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book;

    @BeforeEach
    public void setUp() {
        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setGenre("Fiction");
        book.setAuthor(new Author());
    }

    @Test
    public void testGetAllBooks() {
        Page<Book> bookPage = new PageImpl<>(Collections.singletonList(book));
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(bookPage);

        Page<Book> result = bookService.getAllBooks(PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals(book.getTitle(), result.getContent().get(0).getTitle());
        verify(bookRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    public void testGetBookById_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Book foundBook = bookService.getBookById(1L);

        assertEquals(book.getId(), foundBook.getId());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetBookById_NotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            bookService.getBookById(1L);
        });

        assertEquals("Book not found", exception.getMessage());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    public void testCreateBook() {
        when(bookRepository.save(book)).thenReturn(book);

        Book createdBook = bookService.createBook(book);

        assertEquals(book.getId(), createdBook.getId());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    public void testUpdateBook_Success() {
        when(bookRepository.existsById(1L)).thenReturn(true);
        when(bookRepository.save(book)).thenReturn(book);

        Book updatedBook = bookService.updateBook(1L, book);

        assertEquals(book.getId(), updatedBook.getId());
        verify(bookRepository, times(1)).existsById(1L);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    public void testUpdateBook_NotFound() {
        when(bookRepository.existsById(1L)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            bookService.updateBook(1L, book);
        });

        assertEquals("Book not found", exception.getMessage());
        verify(bookRepository, times(1)).existsById(1L);
    }

    @Test
    public void testDeleteBook_Success() {
        when(bookRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> bookService.deleteBook(1L));
        verify(bookRepository, times(1)).existsById(1L);
        verify(bookRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteBook_NotFound() {
        when(bookRepository.existsById(1L)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            bookService.deleteBook(1L);
        });

        assertEquals("Book not found", exception.getMessage());
        verify(bookRepository, times(1)).existsById(1L);
    }
}
