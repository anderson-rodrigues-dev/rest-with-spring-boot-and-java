package com.example.services;

import com.example.controllers.PersonController;
import com.example.data.vo.v1.PersonVO;
import com.example.exceptions.RequiredObjectIsNullException;
import com.example.exceptions.ResourceNotFoundException;
import com.example.mapper.ModelMapper;
import com.example.models.Person;
import com.example.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.logging.Logger;

@Service
public class PersonServices {
    private final Logger logger = Logger.getLogger(PersonServices.class.getName());

    @Autowired
    PersonRepository repository;

    public List<PersonVO> findAll(){
        var entity = repository.findAll();
        List<PersonVO> voList =  ModelMapper.parseListObjects(entity, PersonVO.class);
        voList.forEach(person -> {
            try {
                person.add(linkTo(methodOn(PersonController.class).findById(person.getKey())).withSelfRel());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return voList;
    }

    public PersonVO findById(Long id) throws Exception {
        logger.info("Finding one person!");
        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
        PersonVO vo = ModelMapper.parseObject(entity, PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());

        return vo;
    }

    public PersonVO create(PersonVO person) throws Exception {
        if(person == null) throw new RequiredObjectIsNullException();

        logger.info("Creating one person!");
        var entity = ModelMapper.parseObject(person, Person.class);
        PersonVO vo = ModelMapper.parseObject(repository.save(entity), PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());

        return vo;
    }

    public PersonVO update(PersonVO person) throws Exception {
        if(person == null) throw new RequiredObjectIsNullException();

        logger.info("Updating one person!");
        var entity = repository.findById(person.getKey())
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());

        PersonVO vo = ModelMapper.parseObject(repository.save(entity), PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());

        return vo;
    }

    @Transactional
    public PersonVO disablePerson(Long id) throws Exception {
        logger.info("Disabling one person!");

        repository.disablePerson(id);

        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));

        PersonVO vo = ModelMapper.parseObject(entity, PersonVO.class);

        vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());

        return vo;
    }

    public void delete(Long id){
        logger.info("Deleting person: " + id);
        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));

        repository.delete(entity);
    }
}
