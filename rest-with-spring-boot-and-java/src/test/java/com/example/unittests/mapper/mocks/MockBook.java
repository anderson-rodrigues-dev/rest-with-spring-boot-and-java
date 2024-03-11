package com.example.unittests.mapper.mocks;

import com.example.data.vo.v1.BookVO;
import com.example.models.Book;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MockBook {

    public List<Book> mockEntityList(){
        List<Book> books = new ArrayList<>();

        for(int i = 0; i < 14; i++){
            books.add(mockEntity(i));
        }

        return books;
    }

    public List<BookVO> mockBookVOList(){
        List<BookVO> books = new ArrayList<>();

        for(int i = 0; i < 14; i++){
            books.add(mockBookVO(i));
        }

        return books;
    }

    public Book mockEntity(){
        return mockEntity(0);
    }

    public Book mockEntity(Integer number){
        Book book = new Book();

        book.setId(number.longValue());
        book.setAuthor("Author" + number);
        book.setLaunchDate(new Date(2024_03_10L));
        book.setPrice(number.doubleValue());
        book.setTitle("Title" + number);

        return book;
    }

    public BookVO mockBookVO(){
        return mockBookVO(0);
    }

    public BookVO mockBookVO(Integer number){
        BookVO book = new BookVO();

        book.setKey(number.longValue());
        book.setAuthor("Author" + number);
        book.setLaunchDate(new Date(2024 - 1900, 2, 10));
        book.setPrice(number.doubleValue());
        book.setTitle("Title" + number);

        return book;
    }
}
