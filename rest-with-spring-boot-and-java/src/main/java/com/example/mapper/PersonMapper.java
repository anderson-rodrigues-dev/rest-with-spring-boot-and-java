package com.example.mapper;

import com.example.data.vo.v2.PersonVOV2;
import com.example.models.Person;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PersonMapper {
    public PersonVOV2 convertEntityToVo(Person person){
        PersonVOV2 vov2 = new PersonVOV2();
        vov2.setId(person.getId());
        vov2.setFirstName(person.getFirstName());
        vov2.setLastName(person.getLastName());
        vov2.setBirthDate(new Date());
        vov2.setAddress(person.getAddress());
        vov2.setGender(person.getGender());

        return vov2;
    }

    public Person convertVoToEntity(PersonVOV2 personVOV2){
        Person person = new Person();
        person.setId(personVOV2.getId());
        person.setFirstName(personVOV2.getFirstName());
        person.setLastName(personVOV2.getLastName());
        person.setAddress(personVOV2.getAddress());
        person.setGender(personVOV2.getGender());

        return person;
    }
}
