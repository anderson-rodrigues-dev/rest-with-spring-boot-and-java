package com.example.integrationtests.controller.withyml;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;

import com.example.configs.TestConfigs;

import com.example.integrationtests.controller.withyml.mapper.YamlMapper;
import com.example.integrationtests.testcontainers.AbstractIntegrationTest;
import com.example.integrationtests.vo.AccountCredentialsVO;
import com.example.integrationtests.vo.BookVO;
import com.example.integrationtests.vo.TokenVO;
import com.example.integrationtests.vo.pagedmodels.PagedModelBook;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class BookControllerYamlTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;

    private static YamlMapper objectMapper;

    private static BookVO book;

    @BeforeAll
    public static void setup() {
        objectMapper = new YamlMapper();
        book = new BookVO();
    }
    
    @Test
    @Order(1)
    public void authorization() {
        AccountCredentialsVO user = new AccountCredentialsVO();
        user.setUsername("leandro");
        user.setPassword("admin123");

        var token =
                given()
                    .config(
                        RestAssuredConfig
                            .config()
                            .encoderConfig(EncoderConfig.encoderConfig()
                                    .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
                    .basePath("/auth/signin")
                    .port(TestConfigs.SERVER_PORT)
                    .contentType(TestConfigs.CONTENT_TYPE_YAML)
    				.accept(TestConfigs.CONTENT_TYPE_YAML)
                    .body(user, objectMapper)
                    .when()
                        .post()
                    .then()
                        .statusCode(200)
                    .extract()
                    .body()
                        .as(TokenVO.class, objectMapper)
                    .getAccessToken();

            specification =
                new RequestSpecBuilder()
                    .addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + token)
                    .setBasePath("/api/book/v1")
                    .setPort(TestConfigs.SERVER_PORT)
                    .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                    .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                    .build();
    }
      
    @Test
    @Order(2)
    public void testCreate() {
        
        mockBook();

        book = given()
                    .config(
                        RestAssuredConfig
                            .config()
                            .encoderConfig(EncoderConfig.encoderConfig()
                                    .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
                    .spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_YAML)
				.accept(TestConfigs.CONTENT_TYPE_YAML)
                    .body(book, objectMapper)
                    .when()
                    .post()
                .then()
                    .statusCode(200)
                        .extract()
                        .body()
                            .as(BookVO.class, objectMapper);
        
        assertNotNull(book.getId());
        assertNotNull(book.getTitle());
        assertNotNull(book.getAuthor());
        assertNotNull(book.getPrice());
        assertTrue(book.getId() > 0);
        assertEquals("Docker Deep Dive", book.getTitle());
        assertEquals("Nigel Poulton", book.getAuthor());
        assertEquals(55.99, book.getPrice());
    }
      
    @Test
    @Order(3)
    public void testUpdate() {
        
        book.setTitle("Docker Deep Dive - Updated");

        BookVO bookUpdated = given()
                    .config(
                        RestAssuredConfig
                            .config()
                            .encoderConfig(EncoderConfig.encoderConfig()
                                    .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
                    .spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_YAML)
				.accept(TestConfigs.CONTENT_TYPE_YAML)
                    .body(book, objectMapper)
                    .when()
                    .put()
                .then()
                    .statusCode(200)
                        .extract()
                        .body()
                        .as(BookVO.class, objectMapper);
        
        assertNotNull(bookUpdated.getId());
        assertNotNull(bookUpdated.getTitle());
        assertNotNull(bookUpdated.getAuthor());
        assertNotNull(bookUpdated.getPrice());
        assertEquals(bookUpdated.getId(), book.getId());
        assertEquals("Docker Deep Dive - Updated", bookUpdated.getTitle());
        assertEquals("Nigel Poulton", bookUpdated.getAuthor());
        assertEquals(55.99, bookUpdated.getPrice());
    }

    @Test
    @Order(4)
    public void testFindById() {
        var foundBook = given()
                    .config(
                        RestAssuredConfig
                            .config()
                            .encoderConfig(EncoderConfig.encoderConfig()
                                    .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
                    .spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_YAML)
				.accept(TestConfigs.CONTENT_TYPE_YAML)
                    .pathParam("id", book.getId())
                    .when()
                    .get("{id}")
                .then()
                    .statusCode(200)
                        .extract()
                        .body()
                        .as(BookVO.class, objectMapper);
        
        assertNotNull(foundBook.getId());
        assertNotNull(foundBook.getTitle());
        assertNotNull(foundBook.getAuthor());
        assertNotNull(foundBook.getPrice());
        assertEquals(foundBook.getId(), book.getId());
        assertEquals("Docker Deep Dive - Updated", foundBook.getTitle());
        assertEquals("Nigel Poulton", foundBook.getAuthor());
        assertEquals(55.99, foundBook.getPrice());
    }
    
    @Test
    @Order(5)
    public void testDelete() {
        given()
            .config(
                RestAssuredConfig
                    .config()
                    .encoderConfig(EncoderConfig.encoderConfig()
                            .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
            .spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_YAML)
				.accept(TestConfigs.CONTENT_TYPE_YAML)
                    .pathParam("id", book.getId())
                    .when()
                    .delete("{id}")
                .then()
                    .statusCode(204);
    }
    
    @Test
    @Order(6)
    public void testFindAll() {
        var response = given()
                    .config(
                        RestAssuredConfig
                            .config()
                            .encoderConfig(EncoderConfig.encoderConfig()
                                    .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
                    .spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_YAML)
				.accept(TestConfigs.CONTENT_TYPE_YAML)
                .queryParams("page", 3, "size", 10, "direction", "asc")
                    .when()
                    .get()
                .then()
                    .statusCode(200)
                        .extract()
                        .body()
                        .as(PagedModelBook.class, objectMapper);


        List<BookVO> books = response.getContent();

        assertNotNull(books);

        for(BookVO book : books){
            assertNotNull(book.getId());
            assertNotNull(book.getTitle());
            assertNotNull(book.getAuthor());
            assertNotNull(book.getPrice());
            assertTrue(book.getId() > 0);
            assertTrue(book.getPrice() > 0);
        }
    }

    @Test
    @Order(7)
    public void testHATEOAS() {
        var content = given()
                .config(
                        RestAssuredConfig
                                .config()
                                .encoderConfig(EncoderConfig.encoderConfig()
                                        .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
                .spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_YAML)
                .accept(TestConfigs.CONTENT_TYPE_YAML)
                .queryParams("page", 1, "limit", 10, "direction", "asc")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();


        assertNotNull(content);

        assertTrue(content.contains("links:\n  - rel: \"self\"\n    href: \"http://localhost:8888/api/book/v1/10\""));
        assertTrue(content.contains("links:\n  - rel: \"self\"\n    href: \"http://localhost:8888/api/book/v1/1\""));
        assertTrue(content.contains("links:\n  - rel: \"self\"\n    href: \"http://localhost:8888/api/book/v1/14\""));
        assertTrue(content.contains("links:\n  - rel: \"self\"\n    href: \"http://localhost:8888/api/book/v1/6\""));

        assertTrue(content.contains("- rel: \"first\"\n  href: \"http://localhost:8888/api/book/v1?limit=10&direction=title%3A%20ASC&page=0&size=10&sort=title,asc\""));
        assertTrue(content.contains("- rel: \"prev\"\n  href: \"http://localhost:8888/api/book/v1?limit=10&direction=title%3A%20ASC&page=0&size=10&sort=title,asc\""));
        assertTrue(content.contains("- rel: \"self\"\n  href: \"http://localhost:8888/api/book/v1?page=1&limit=10&direction=title%3A%20ASC\""));
        assertTrue(content.contains("- rel: \"last\"\n  href: \"http://localhost:8888/api/book/v1?limit=10&direction=title%3A%20ASC&page=1&size=10&sort=title,asc\""));

        assertTrue(content.contains("page:\n  size: 10\n  totalElements: 15\n  totalPages: 2\n  number: 1"));
    }
     
    private void mockBook() {
        book.setTitle("Docker Deep Dive");
        book.setAuthor("Nigel Poulton");
        book.setPrice(55.99);
        book.setLaunchDate(new Date());
    }
}