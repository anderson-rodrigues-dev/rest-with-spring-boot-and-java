package com.example.services;

import com.example.controllers.BookController;
import com.example.data.vo.v1.BookVO;
import com.example.exceptions.RequiredObjectIsNullException;
import com.example.exceptions.ResourceNotFoundException;
import com.example.mapper.ModelMapper;
import com.example.models.Book;
import com.example.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class BookServices {
    private final Logger logger = Logger.getLogger(BookServices.class.getName());

    @Autowired
    private BookRepository repository;

    public List<BookVO> findAll(){
        logger.info("Finding all books!");

        var entity = repository.findAll();

        List<BookVO> voList = ModelMapper.parseListObjects(entity, BookVO.class);
        voList.forEach(book -> {
            try{
                book.add(linkTo(methodOn(BookController.class).findById(book.getKey())).withSelfRel());
            } catch (Exception e){
                throw new RuntimeException(e);
            }
        });

        return voList;
    }

    public BookVO findById(Long id) {
        if(id == null) throw new RequiredObjectIsNullException();

        logger.info("Finding one book!");
        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));

        BookVO vo = ModelMapper.parseObject(entity, BookVO.class);
        vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());

        return vo;
    }

    public BookVO create(BookVO book){
        if(book == null) throw new RequiredObjectIsNullException();

        logger.info("Creating one book!");
        Book entity = ModelMapper.parseObject(book, Book.class);
        BookVO vo = ModelMapper.parseObject(repository.save(entity), BookVO.class);
        vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());

        return vo;
    }

    public BookVO update(BookVO book){
        if(book == null) throw new RequiredObjectIsNullException();

        logger.info("Updating one book!");

        var entity = repository.findById(book.getKey())
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
        entity.setAuthor(book.getAuthor());
        entity.setLaunchDate(book.getLaunchDate());
        entity.setPrice(book.getPrice());
        entity.setTitle(book.getTitle());

        BookVO vo = ModelMapper.parseObject(repository.save(entity), BookVO.class);
        vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());

        return vo;
    }

    public void delete(Long id){
        if(id == null) throw new RequiredObjectIsNullException();

        Book entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
        logger.info("Deleting person: " + entity.getId());
        repository.delete(entity);
    }
}
