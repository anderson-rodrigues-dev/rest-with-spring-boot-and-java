package com.example.integrationtests.controller.withjson;

import com.example.configs.TestConfigs;
import com.example.integrationtests.testcontainers.AbstractIntegrationTest;
import com.example.integrationtests.vo.AccountCredentialsVO;
import com.example.integrationtests.vo.PersonVO;
import com.example.integrationtests.vo.TokenVO;
import com.example.integrationtests.vo.wrappers.WrapperPersonVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonControllerJsonTest extends AbstractIntegrationTest {
    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;

    private static PersonVO person;

    @BeforeAll
    public static void setUp(){
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        person = new PersonVO();
    }

    @Test
    @Order(0)
    void authorization() {
        AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");
        String accessToken = given()
                .basePath("/auth/signin")
                .port(TestConfigs.SERVER_PORT)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
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
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
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
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
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
    void testFindById() throws JsonProcessingException {
        var content = given()
                .spec(specification)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCALHOST)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
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
        assertTrue(persistedPerson.getEnabled());

        assertTrue(persistedPerson.getId() > 0);

        assertEquals("Richard", persistedPerson.getFirstName());
        assertEquals("Stallman", persistedPerson.getLastName());
        assertEquals("New York City, New York, US", persistedPerson.getAddress());
        assertEquals("Male", persistedPerson.getGender());
    }

    @Test
    @Order(4)
    void testDisablePersonById() throws JsonProcessingException {
        var content = given()
                .spec(specification)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCALHOST)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
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
        assertFalse(persistedPerson.getEnabled());

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
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
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
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .accept(TestConfigs.CONTENT_TYPE_JSON)
                .queryParams("page", 3, "size", 10, "direction", "asc")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        assertNotNull(content);

        WrapperPersonVO wrapper = objectMapper.readValue(content, WrapperPersonVO.class);

        List<PersonVO> people = wrapper.getEmbedded().getPersons();

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
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .pathParam("firstName", person.getFirstName())
                .when()
                .get("findPersonsByName/{firstName}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        assertNotNull(content);

        WrapperPersonVO wrapper = objectMapper.readValue(content, WrapperPersonVO.class);

        List<PersonVO> people = wrapper.getEmbedded().getPersons();

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
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .accept(TestConfigs.CONTENT_TYPE_JSON)
                .queryParams("page", 3, "limit", 20, "direction", "asc")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        assertNotNull(content);

        assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/person/v1/376\"}}}"));
        assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/person/v1/1000\"}}}"));
        assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/person/v1/507\"}}}"));
        assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/person/v1/958\"}}}"));

        assertTrue(content.contains("\"first\":{\"href\":\"http://localhost:8888/api/person/v1?limit=20&direction=firstName%3A%20ASC&page=0&size=20&sort=firstName,asc\"}"));
        assertTrue(content.contains("\"prev\":{\"href\":\"http://localhost:8888/api/person/v1?limit=20&direction=firstName%3A%20ASC&page=2&size=20&sort=firstName,asc\"}"));
        assertTrue(content.contains("\"self\":{\"href\":\"http://localhost:8888/api/person/v1?page=3&limit=20&direction=firstName%3A%20ASC\"}"));
        assertTrue(content.contains("\"next\":{\"href\":\"http://localhost:8888/api/person/v1?limit=20&direction=firstName%3A%20ASC&page=4&size=20&sort=firstName,asc\"}"));
        assertTrue(content.contains("\"last\":{\"href\":\"http://localhost:8888/api/person/v1?limit=20&direction=firstName%3A%20ASC&page=50&size=20&sort=firstName,asc\"}}"));

        assertTrue(content.contains("\"page\":{\"size\":20,\"totalElements\":1005,\"totalPages\":51,\"number\":3}"));
    }

    private void mockPerson() {
        person.setFirstName("Richard");
        person.setLastName("Stallman");
        person.setAddress("New York City, New York, US");
        person.setGender("Male");
        person.setEnabled(true);
    }
}
