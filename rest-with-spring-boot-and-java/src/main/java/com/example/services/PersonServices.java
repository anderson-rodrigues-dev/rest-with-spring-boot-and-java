package com.example.services;

import com.example.models.Person;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class PersonServices {
    private final AtomicLong counter = new AtomicLong();
    private final Logger logger = Logger.getLogger(PersonServices.class.getName());

    public List<Person> findAll(){
        List<Person> persons = new ArrayList<Person>();
        for(int i = 1; i <= 8; i++){
            Person person = mockPerson(i);
            persons.add(person);
        }
        return persons;
    }

    public Person findById(String id){
        logger.info("Finding one person!");
        Person person = new Person();
        person.setId(counter.incrementAndGet());
        person.setFirstName("Anderson");
        person.setLastName("Alves");
        person.setAddress("Diadema - São Paulo - Brasil");
        person.setGender("Male");
        return person;
    }

    private Person mockPerson(int i) {
        logger.info("Finding all persons");
        Person person = new Person();
        person.setId(counter.incrementAndGet());
        person.setFirstName("Person name " + i);
        person.setLastName("Last Name " + i);
        person.setAddress("Some address in Brasil " + i);
        person.setGender(i%2 == 0 ? "Male" : "Female");
        return person;
    }

    public Person create(Person person){
        logger.info("Creating one person!");
        person.setId(counter.incrementAndGet());
        return person;
    }

    public Person update(Person person){
        logger.info("Updating one person!");
        return person;
    }

    public List<Person> delete(String id){
        logger.info("Deleting person: " + id);
        List<Person> persons = new ArrayList<Person>();
        persons = this.findAll();
        return persons.stream().filter(person -> !person.getId().toString().equals(id)).collect(Collectors.toList());
    }
}
