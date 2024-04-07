package com.example.unittests.mockito.services;

import com.example.data.vo.v1.BookVO;
import com.example.exceptions.RequiredObjectIsNullException;
import com.example.models.Book;
import com.example.repositories.BookRepository;
import com.example.services.BookServices;
import com.example.unittests.mapper.mocks.MockBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class BookServicesTest {
    MockBook input;

    @InjectMocks
    private BookServices service;

    @Mock
    BookRepository repository;

    @BeforeEach
    void setUpMocks() {
        input = new MockBook();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findById() {
        Book entity = input.mockEntity(1);

        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));
        var result = service.findById(entity.getId());
        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(result.getLinks());
        assertTrue(result.toString().contains("links: [</api/book/v1/" + result.getKey() + ">;rel=\"self\"]"));
        assertEquals("Author" + result.getKey(), result.getAuthor());
        assertThat(result.getLaunchDate()).isEqualTo(LocalDate.of(2024, 3, 10));
        assertEquals(result.getKey().doubleValue(), result.getPrice());
        assertEquals("Title" + result.getKey(), result.getTitle());
    }

    @Test
    void create() {
        Book entity = input.mockEntity(1);

        BookVO vo = input.mockBookVO(1);
        when(repository.save(entity)).thenReturn(entity);
        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));

        BookVO result = service.create(vo);

        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(result.getLinks());

        assertTrue(result.toString().endsWith("links: [</api/book/v1/" + result.getKey() + ">;rel=\"self\"]"));
        assertThat(result.getAuthor()).isEqualTo("Author" + result.getKey());
        assertThat(result.getLaunchDate()).isEqualTo(LocalDate.of(2024, 3, 10));
        assertThat(result.getPrice()).isEqualTo(result.getKey().doubleValue());
        assertThat(result.getTitle()).isEqualTo("Title" + result.getKey());
    }


    @Test
    void createWithNullBook(){
        Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> service.create(null));

        String expectedMessage = "It is not allowed to persist a null object!";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void update() {
        Book entity = input.mockEntity(1);

        BookVO vo = input.mockBookVO(1);
        when(repository.save(entity)).thenReturn(entity);
        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));

        var result = service.update(vo);
        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(result.getLinks());
        assertTrue(result.toString().contains("links: [</api/book/v1/" + result.getKey() + ">;rel=\"self\"]"));
        assertEquals("Author" + result.getKey(), result.getAuthor());
        assertEquals(new Date(2024_03_10L), result.getLaunchDate());
        assertEquals(result.getKey().doubleValue(), result.getPrice());
        assertEquals("Title" + result.getKey(), result.getTitle());
    }

    @Test
    void delete() {
        Book book = input.mockEntity(1);

        when(repository.findById(book.getId())).thenReturn(Optional.of(book));
        service.delete(book.getId());
        assertTrue(true);
    }
}