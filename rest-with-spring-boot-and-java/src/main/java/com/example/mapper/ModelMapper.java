package com.example.mapper;


// import com.github.dozermapper.core.DozerBeanMapperBuilder;
// import com.github.dozermapper.core.Mapper;

import com.example.data.vo.v1.BookVO;
import com.example.data.vo.v1.PersonVO;
import com.example.models.Book;
import com.example.models.Person;

import java.util.ArrayList;
import java.util.List;

public class ModelMapper {
    // private static Mapper mapper = DozerBeanMapperBuilder.buildDefault();
    private static final org.modelmapper.ModelMapper mapper = new org.modelmapper.ModelMapper();

    static {
        mapper.createTypeMap(
                    Person.class,
                    PersonVO.class)
                .addMapping(Person::getId, PersonVO::setKey);
        mapper.createTypeMap(
                        PersonVO.class,
                        Person.class)
                .addMapping(PersonVO::getKey, Person::setId);
        mapper.createTypeMap(
                Book.class,
                BookVO.class
        ).addMapping(Book::getId, BookVO::setKey);
        mapper.createTypeMap(
                BookVO.class,
                Book.class
        ).addMapping(BookVO::getKey, Book::setId);
    }

    public static <O, D> D parseObject(O origin, Class<D> destination){
        return mapper.map(origin, destination);
    }

    public static <O, D> List<D> parseListObjects(List<O> origin, Class<D> destination){
        List<D> destinationObjects = new ArrayList<>();
        for(O o : origin){
            destinationObjects.add(mapper.map(o, destination));
        }
        return destinationObjects;
    }

}
