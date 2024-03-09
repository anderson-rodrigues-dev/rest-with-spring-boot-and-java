package com.example.controllers;

import com.example.data.vo.v1.PersonVO;
import com.example.services.PersonServices;
import com.example.util.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/person/v1")
public class PersonController {
    @Autowired
    private PersonServices service;

    @GetMapping(value = "/{id}",
                produces = {com.example.util.MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
    public PersonVO findById(@PathVariable(value = "id") Long id) throws Exception{
        return service.findById(id);
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
    public List<PersonVO> findAll() throws Exception{
        return service.findAll();
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML},
                produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
    public PersonVO create(@RequestBody PersonVO person) throws Exception {
        return service.create(person);
    }

    @PutMapping(consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML},
                produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
    public PersonVO update(@RequestBody PersonVO person) throws Exception {
        return service.update(person);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable(value = "id") Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
