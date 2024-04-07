package com.example.unittests.mockito.services;

import com.example.data.vo.v1.PersonVO;
import com.example.exceptions.RequiredObjectIsNullException;
import com.example.models.Person;
import com.example.repositories.PersonRepository;
import com.example.services.PersonServices;
import com.example.unittests.mapper.mocks.MockPerson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class PersonServicesTest {
    MockPerson input;

    @InjectMocks
    private PersonServices service;

    @Mock
    PersonRepository repository;

    @BeforeEach
    void setUpMocks(){
        input = new MockPerson();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findById() throws Exception {
        Person entity = input.mockEntity(1);
        entity.setId(1L);

        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));
        var result = service.findById(entity.getId());
        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(result.getLinks());
        assertTrue(result.toString().contains("links: [</api/person/v1/1>;rel=\"self\"]"));
        assertEquals("Address Test1", result.getAddress());
        assertEquals("First Name Test1", result.getFirstName());
        assertEquals("Last Name Test1", result.getLastName());
        assertEquals("Female", result.getGender());
    }

    @Test
    void create() throws Exception {
        Person entity = input.mockEntity(1);

        entity.setId(1L);

        PersonVO vo = input.mockVO(1);
        vo.setKey(1L);

        when(repository.save(entity)).thenReturn(entity);
        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));

        var result = service.create(vo);
        System.out.println(result.getKey() + result.getFirstName() + result.getLastName() + result.getAddress() + result.getGender());
        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(result.getLinks());
        assertTrue(result.toString().contains("links: [</api/person/v1/1>;rel=\"self\"]"));
        assertEquals("Address Test1", result.getAddress());
        assertEquals("First Name Test1", result.getFirstName());
        assertEquals("Last Name Test1", result.getLastName());
        assertEquals("Female", result.getGender());
    }

    @Test
    void createWithNullPerson() {

        Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> service.create(null));

        String expectedMessage = "It is not allowed to persist a null object!";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void update() throws Exception {
        Person entity = input.mockEntity(1);
        entity.setId(1L);

        PersonVO vo = input.mockVO(1);
        vo.setKey(1L);

        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);

        var result = service.update(vo);

        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(result.getLinks());
        assertTrue(result.toString().contains("links: [</api/person/v1/1>;rel=\"self\"]"));
        assertEquals("Address Test1", result.getAddress());
        assertEquals("First Name Test1", result.getFirstName());
        assertEquals("Last Name Test1", result.getLastName());
        assertEquals("Female", result.getGender());
    }

    @Test
    void updateWithNullPerson() {

        Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> service.update(null));

        String expectedMessage = "It is not allowed to persist a null object!";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

    }

    @Test
    void delete() {
        Person entity = input.mockEntity(1);
        entity.setId(1L);

        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));
        service.delete(entity.getId());
        assertTrue(true);
    }
}