package com.example.integrationtests.controller.withyml;

import com.example.configs.TestConfigs;
import com.example.integrationtests.controller.withyml.mapper.YamlMapper;
import com.example.integrationtests.testcontainers.AbstractIntegrationTest;
import com.example.integrationtests.vo.AccountCredentialsVO;
import com.example.integrationtests.vo.PersonVO;
import com.example.integrationtests.vo.TokenVO;
import com.example.integrationtests.vo.pagedmodels.PagedModelPerson;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonControllerYamlTest extends AbstractIntegrationTest {
    private static RequestSpecification specification;
    private static YamlMapper mapper;

    private static PersonVO person;

    @BeforeAll
    public static void setUp(){
        mapper = new YamlMapper();

        person = new PersonVO();
    }

    @Test
    @Order(0)
    void authorization() {
        AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");
        String accessToken = given()
                .config(RestAssuredConfig.config()
                        .encoderConfig(EncoderConfig.encoderConfig()
                                .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)
                        )
                )
                .accept(TestConfigs.CONTENT_TYPE_YAML)
                .basePath("/auth/signin")
                .port(TestConfigs.SERVER_PORT)
                .contentType(TestConfigs.CONTENT_TYPE_YAML)
                    .body(user, mapper)
                    .when()
                    .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                    .as(TokenVO.class, mapper)
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
    void testCreate() {
        mockPerson();

        PersonVO createdPerson = given()
                .spec(specification)
                .config(RestAssuredConfig.config()
                        .encoderConfig(EncoderConfig.encoderConfig()
                                .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)
                        )
                )
                .accept(TestConfigs.CONTENT_TYPE_YAML)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCALHOST)
                .contentType(TestConfigs.CONTENT_TYPE_YAML)
                .body(person, mapper)
                    .when()
                    .post()
                .then()
                    .statusCode(200)
                .extract()
                    .body()
                        .as(PersonVO.class, mapper);

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
                .config(RestAssuredConfig.config()
                        .encoderConfig(EncoderConfig.encoderConfig()
                                .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)
                        )
                )
                .accept(TestConfigs.CONTENT_TYPE_YAML)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ANDIIN)
                .contentType(TestConfigs.CONTENT_TYPE_YAML)
                .body(person, mapper)
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
    void testDisablePersonById() {
        PersonVO persistedPerson = given()
                .spec(specification)
                .config(RestAssuredConfig.config()
                        .encoderConfig(EncoderConfig.encoderConfig()
                                .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)
                        )
                )
                .accept(TestConfigs.CONTENT_TYPE_YAML)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCALHOST)
                .contentType(TestConfigs.CONTENT_TYPE_YAML)
                .pathParam("id", person.getId())
                .when()
                .patch("{id}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(PersonVO.class, mapper);

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
    @Order(4)
    void testFindById() {
        PersonVO persistedPerson = given()
                .spec(specification)
                .config(RestAssuredConfig.config()
                        .encoderConfig(EncoderConfig.encoderConfig()
                                .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)
                        )
                )
                .accept(TestConfigs.CONTENT_TYPE_YAML)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCALHOST)
                .contentType(TestConfigs.CONTENT_TYPE_YAML)
                .pathParam("id", person.getId())
                .when()
                .get("{id}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(PersonVO.class, mapper);

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
                .config(RestAssuredConfig.config()
                        .encoderConfig(EncoderConfig.encoderConfig()
                                .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)
                        )
                )
                .accept(TestConfigs.CONTENT_TYPE_YAML)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ANDIIN)
                .contentType(TestConfigs.CONTENT_TYPE_YAML)
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
    void testFindAll() {
        var paged = given()
                .spec(specification)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCALHOST)
                .contentType(TestConfigs.CONTENT_TYPE_YAML)
                .accept(TestConfigs.CONTENT_TYPE_YAML)
                .queryParams("page", 3, "limit", 20, "direction", "asc")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(PagedModelPerson.class, mapper);

        assertNotNull(paged);

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
    @Order(8)
    void testHateoas() {
        var content = given()
                .spec(specification)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCALHOST)
                .contentType(TestConfigs.CONTENT_TYPE_YAML)
                .accept(TestConfigs.CONTENT_TYPE_YAML)
                .queryParams("page", 3, "size", 10, "direction", "asc")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        assertNotNull(content);

        assertTrue(content.contains("links:\n  - rel: \"self\"\n    href: \"http://localhost:8888/api/person/v1/376\""));
        assertTrue(content.contains("links:\n  - rel: \"self\"\n    href: \"http://localhost:8888/api/person/v1/1000\""));
        assertTrue(content.contains("links:\n  - rel: \"self\"\n    href: \"http://localhost:8888/api/person/v1/507\""));
        assertTrue(content.contains("links:\n  - rel: \"self\"\n    href: \"http://localhost:8888/api/person/v1/958\""));

        assertTrue(content.contains("- rel: \"first\"\n  href: \"http://localhost:8888/api/person/v1?limit=20&direction=firstName%3A%20ASC&page=0&size=20&sort=firstName,asc\""));
        assertTrue(content.contains("- rel: \"prev\"\n  href: \"http://localhost:8888/api/person/v1?limit=20&direction=firstName%3A%20ASC&page=2&size=20&sort=firstName,asc\""));
        assertTrue(content.contains("- rel: \"self\"\n  href: \"http://localhost:8888/api/person/v1?page=3&limit=20&direction=firstName%3A%20ASC\""));
        assertTrue(content.contains("- rel: \"next\"\n  href: \"http://localhost:8888/api/person/v1?limit=20&direction=firstName%3A%20ASC&page=4&size=20&sort=firstName,asc\""));
        assertTrue(content.contains("- rel: \"last\"\n  href: \"http://localhost:8888/api/person/v1?limit=20&direction=firstName%3A%20ASC&page=50&size=20&sort=firstName,asc\""));

        assertTrue(content.contains("page:\n  size: 20\n  totalElements: 1005\n  totalPages: 51\n  number: 3"));
    }

    private void mockPerson() {
        person.setFirstName("Richard");
        person.setLastName("Stallman");
        person.setAddress("New York City, New York, US");
        person.setGender("Male");
        person.setEnabled(true);
    }
}
