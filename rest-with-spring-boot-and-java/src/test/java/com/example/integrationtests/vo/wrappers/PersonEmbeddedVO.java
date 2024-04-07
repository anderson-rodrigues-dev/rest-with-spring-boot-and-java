package com.example.integrationtests.vo.wrappers;

import com.example.integrationtests.vo.PersonVO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@XmlRootElement
public class PersonEmbeddedVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("personVOList")
    private List<PersonVO> persons;

    public PersonEmbeddedVO() {}

    public List<PersonVO> getPersons() {
        return persons;
    }

    public void setPersons(List<PersonVO> persons) {
        this.persons = persons;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonEmbeddedVO that = (PersonEmbeddedVO) o;
        return Objects.equals(persons, that.persons);
    }

    @Override
    public int hashCode() {
        return Objects.hash(persons);
    }
}
