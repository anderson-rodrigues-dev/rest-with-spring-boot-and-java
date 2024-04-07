package com.example.integrationtests.repositories;

import com.example.integrationtests.testcontainers.AbstractIntegrationTest;
import com.example.models.Person;
import com.example.repositories.PersonRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonRepositoryTest extends AbstractIntegrationTest {
    @Autowired
    PersonRepository repository;

    private static Person person;

    @BeforeAll
    public static void setup(){
        person = new Person();
    }

    @Test
    @Order(1)
    void findPersonsByNameTest(){
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "firstName"));
        person = repository.findPersonsByName("aar", pageable).getContent().getFirst();

        assertNotNull(person.getId());
        assertNotNull(person.getFirstName());
        assertNotNull(person.getLastName());
        assertNotNull(person.getAddress());
        assertNotNull(person.getGender());
        assertNotNull(person.getEnabled());

        assertTrue(person.getId() > 0);
    }

    @Test
    @Order(2)
    void disablePersonTest(){
        repository.disablePerson(person.getId());

        Person entityDisabled = repository.findById(person.getId()).orElseThrow();

        assertNotNull(entityDisabled);
        assertFalse(entityDisabled.getEnabled());
    }
}
