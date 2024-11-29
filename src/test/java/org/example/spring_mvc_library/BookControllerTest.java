package org.example.spring_mvc_library;

import org.example.spring_mvc_library.controller.BookController;
import org.example.spring_mvc_library.models.Author;
import org.example.spring_mvc_library.models.Book;
import org.example.spring_mvc_library.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    private Book book;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setGenre("Fiction");
        book.setAuthor(new Author());
    }

    @Test
    public void testGetAllBooks() throws Exception {
        Page<Book> bookPage = new PageImpl<>(Collections.singletonList(book));
        when(bookService.getAllBooks(any(Pageable.class))).thenReturn(bookPage);

        mockMvc.perform(get("/api/books")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Test Book"));
    }

    @Test
    public void testGetBookById_Success() throws Exception {
        when(bookService.getBookById(1L)).thenReturn(book);

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Book"));
    }

    @Test
    public void testCreateBook() throws Exception {
        when(bookService.createBook(any(Book.class))).thenReturn(book);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Test Book\",\"genre\":\"Fiction\",\"author\":{}}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Test Book"));
    }

    @Test
    public void testUpdateBook_Success() throws Exception {
        Book updatedBook = new Book();
        updatedBook.setId(1L);
        updatedBook.setTitle("Updated Book");
        updatedBook.setGenre("Fiction");
        updatedBook.setAuthor(new Author());

        when(bookService.updateBook(any(Long.class), any(Book.class))).thenReturn(updatedBook);

        mockMvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated Book\",\"genre\":\"Fiction\",\"author\":{}}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Updated Book"));
    }


    @Test
    public void testDeleteBook_Success() throws Exception {
        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());
    }
}
