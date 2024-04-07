package com.example.integrationtests.controller.withxml;

import com.example.configs.TestConfigs;
import com.example.integrationtests.testcontainers.AbstractIntegrationTest;
import com.example.integrationtests.vo.AccountCredentialsVO;
import com.example.integrationtests.vo.PersonVO;
import com.example.integrationtests.vo.TokenVO;
import com.example.integrationtests.vo.pagedmodels.PagedModelPerson;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class PersonControllerXmlTest extends AbstractIntegrationTest {
    private static RequestSpecification specification;
    private static XmlMapper objectMapper;
    private static PersonVO person;

    @BeforeAll
    public static void setUp(){
        objectMapper = new XmlMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        person = new PersonVO();
    }
    @Test
    @Order(0)
    void authorization() {
        AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");
        var accessToken = given()
                .basePath("/auth/signin")
                .port(TestConfigs.SERVER_PORT)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .accept(TestConfigs.CONTENT_TYPE_XML)
                    .body(user)
                    .when()
                    .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                    .as(TokenVO.class)
                .getAccessToken();

        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + accessToken)
                .setBasePath("/api/person/v1")
                .setPort(TestConfigs.SERVER_PORT)
                    .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                    .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

    @Test
    @Order(1)
    void testCreate() throws JsonProcessingException {
        mockPerson();

        var content = given()
                .spec(specification)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCALHOST)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .accept(TestConfigs.CONTENT_TYPE_XML)
                .body(person)
                    .when()
                    .post()
                .then()
                    .statusCode(200)
                .extract()
                    .body()
                        .asString();

        PersonVO createdPerson = objectMapper.readValue(content, PersonVO.class);
        person = createdPerson;

        assertNotNull(createdPerson);
        assertNotNull(createdPerson.getId());
        assertNotNull(createdPerson.getFirstName());
        assertNotNull(createdPerson.getFirstName());
        assertNotNull(createdPerson.getAddress());
        assertNotNull(createdPerson.getGender());
        assertTrue(createdPerson.getEnabled());

        assertTrue(createdPerson.getId() > 0);

        assertEquals("Richard", createdPerson.getFirstName());
        assertEquals("Stallman", createdPerson.getLastName());
        assertEquals("New York City, New York, US", createdPerson.getAddress());
        assertEquals("Male", createdPerson.getGender());
    }

    @Test
    @Order(2)
    void testCreateWithWrongOrigin() {
        var content = given()
                .spec(specification)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ANDIIN)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .accept(TestConfigs.CONTENT_TYPE_XML)
                .body(person)
                .when()
                .post()
                .then()
                .statusCode(403)
                .extract()
                .body()
                .asString();


        assertNotNull(content);
        assertEquals("Invalid CORS request", content);
    }

    @Test
    @Order(3)
    void testDisablePersonById() throws JsonProcessingException {
        var content = given()
                .spec(specification)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCALHOST)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .accept(TestConfigs.CONTENT_TYPE_XML)
                .pathParam("id", person.getId())
                .when()
                .patch("{id}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
        person = persistedPerson;

        assertNotNull(persistedPerson);
        assertNotNull(persistedPerson.getId());
        assertNotNull(persistedPerson.getFirstName());
        assertNotNull(persistedPerson.getFirstName());
        assertNotNull(persistedPerson.getAddress());
        assertNotNull(persistedPerson.getGender());
        Assertions.assertFalse(persistedPerson.getEnabled());

        assertTrue(persistedPerson.getId() > 0);

        assertEquals("Richard", persistedPerson.getFirstName());
        assertEquals("Stallman", persistedPerson.getLastName());
        assertEquals("New York City, New York, US", persistedPerson.getAddress());
        assertEquals("Male", persistedPerson.getGender());
    }

    @Test
    @Order(4)
    void testFindById() throws JsonProcessingException {
        var content = given()
                .spec(specification)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCALHOST)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .accept(TestConfigs.CONTENT_TYPE_XML)
                .pathParam("id", person.getId())
                .when()
                .get("{id}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
        person = persistedPerson;

        assertNotNull(persistedPerson);
        assertNotNull(persistedPerson.getId());
        assertNotNull(persistedPerson.getFirstName());
        assertNotNull(persistedPerson.getFirstName());
        assertNotNull(persistedPerson.getAddress());
        assertNotNull(persistedPerson.getGender());
        Assertions.assertFalse(persistedPerson.getEnabled());

        assertTrue(persistedPerson.getId() > 0);

        assertEquals("Richard", persistedPerson.getFirstName());
        assertEquals("Stallman", persistedPerson.getLastName());
        assertEquals("New York City, New York, US", persistedPerson.getAddress());
        assertEquals("Male", persistedPerson.getGender());
    }

    @Test
    @Order(5)
    void testFindByIdWithWrongOrigin() {
        var content = given()
                .spec(specification)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ANDIIN)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .accept(TestConfigs.CONTENT_TYPE_XML)
                .pathParam("id", person.getId())
                .when()
                .get("{id}")
                .then()
                .statusCode(403)
                .extract()
                .body()
                .asString();

        assertNotNull(content);
        assertEquals("Invalid CORS request", content);
    }

    @Test
    @Order(6)
    void testFindAll() throws JsonProcessingException {
        var content = given()
                .spec(specification)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCALHOST)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .accept(TestConfigs.CONTENT_TYPE_XML)
                .queryParams("page", 3, "size", 10, "direction", "asc")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        assertNotNull(content);

        PagedModelPerson paged = objectMapper.readValue(content, PagedModelPerson.class);

        List<PersonVO> people = paged.getContent();

        assertNotNull(people);

        for(PersonVO person : people){
            assertNotNull(person.getId());
            assertNotNull(person.getFirstName());
            assertNotNull(person.getLastName());
            assertNotNull(person.getAddress());
            assertNotNull(person.getGender());
            assertNotNull(person.getEnabled());

            assertTrue(person.getId() > 0);
        }
    }

    @Test
    @Order(7)
    void testFindByName() throws JsonProcessingException {
        var content = given()
                .spec(specification)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCALHOST)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .accept(TestConfigs.CONTENT_TYPE_XML)
                .pathParam("firstName", person.getFirstName())
                .when()
                .get("findPersonsByName/{firstName}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        assertNotNull(content);

        PagedModelPerson wrapper = objectMapper.readValue(content, PagedModelPerson.class);

        List<PersonVO> people = wrapper.getContent();

        assertNotNull(people);

        for(PersonVO p : people){
            assertNotNull(p.getId());
            assertNotNull(p.getFirstName());
            assertNotNull(p.getLastName());
            assertNotNull(p.getAddress());
            assertNotNull(p.getGender());
            assertNotNull(p.getEnabled());

            assertTrue(p.getId() > 0);

            assertTrue(p.getFirstName().contains(person.getFirstName()));
        }
    }

    @Test
    @Order(8)
    void testHateoas() {
        var content = given()
                .spec(specification)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCALHOST)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .accept(TestConfigs.CONTENT_TYPE_XML)
                .queryParams("page", 3, "limit", 20, "direction", "asc")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        assertNotNull(content);

        assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/api/person/v1/376</href></links>"));
        assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/api/person/v1/1000</href></links>"));
        assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/api/person/v1/507</href></links>"));
        assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/api/person/v1/958</href></links>"));

        assertTrue(content.contains("<links><rel>first</rel><href>http://localhost:8888/api/person/v1?limit=20&amp;direction=firstName%3A%20ASC&amp;page=0&amp;size=20&amp;sort=firstName,asc</href></links>"));
        assertTrue(content.contains("<links><rel>prev</rel><href>http://localhost:8888/api/person/v1?limit=20&amp;direction=firstName%3A%20ASC&amp;page=2&amp;size=20&amp;sort=firstName,asc</href></links>"));
        assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/api/person/v1?page=3&amp;limit=20&amp;direction=firstName%3A%20ASC</href></links>"));
        assertTrue(content.contains("<links><rel>next</rel><href>http://localhost:8888/api/person/v1?limit=20&amp;direction=firstName%3A%20ASC&amp;page=4&amp;size=20&amp;sort=firstName,asc</href></links>"));
        assertTrue(content.contains("<links><rel>last</rel><href>http://localhost:8888/api/person/v1?limit=20&amp;direction=firstName%3A%20ASC&amp;page=50&amp;size=20&amp;sort=firstName,asc</href></links>"));

        assertTrue(content.contains("<page><size>20</size><totalElements>1005</totalElements><totalPages>51</totalPages><number>3</number></page>"));
    }

    private void mockPerson() {
        person.setFirstName("Richard");
        person.setLastName("Stallman");
        person.setAddress("New York City, New York, US");
        person.setGender("Male");
        person.setEnabled(true);
    }
}
