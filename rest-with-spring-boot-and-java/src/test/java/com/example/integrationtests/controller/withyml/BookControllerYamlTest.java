package com.example.integrationtests.controller.withyml;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.example.configs.TestConfigs;
import com.example.data.vo.v1.BookVO;
import com.example.integrationtests.controller.withyml.mapper.YamlMapper;
import com.example.integrationtests.testcontainers.AbstractIntegrationTest;
import com.example.integrationtests.vo.AccountCredentialsVO;
import com.example.integrationtests.vo.TokenVO;
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
        
        assertNotNull(book.getKey());
        assertNotNull(book.getTitle());
        assertNotNull(book.getAuthor());
        assertNotNull(book.getPrice());
        assertTrue(book.getKey() > 0);
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
        
        assertNotNull(bookUpdated.getKey());
        assertNotNull(bookUpdated.getTitle());
        assertNotNull(bookUpdated.getAuthor());
        assertNotNull(bookUpdated.getPrice());
        assertEquals(bookUpdated.getKey(), book.getKey());
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
                    .pathParam("id", book.getKey())
                    .when()
                    .get("{id}")
                .then()
                    .statusCode(200)
                        .extract()
                        .body()
                        .as(BookVO.class, objectMapper);
        
        assertNotNull(foundBook.getKey());
        assertNotNull(foundBook.getTitle());
        assertNotNull(foundBook.getAuthor());
        assertNotNull(foundBook.getPrice());
        assertEquals(foundBook.getKey(), book.getKey());
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
                    .pathParam("id", book.getKey())
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
                    .when()
                    .get()
                .then()
                    .statusCode(200)
                        .extract()
                        .body()
                        .as(BookVO[].class, objectMapper); 


        List<BookVO> content = Arrays.asList(response);

        BookVO foundBookOne = content.getFirst();
        
        assertNotNull(foundBookOne.getKey());
        assertNotNull(foundBookOne.getTitle());
        assertNotNull(foundBookOne.getAuthor());
        assertNotNull(foundBookOne.getPrice());
        assertTrue(foundBookOne.getKey() > 0);
        assertEquals("Working effectively with legacy code", foundBookOne.getTitle());
        assertEquals("Michael C. Feathers", foundBookOne.getAuthor());
        assertEquals(49.00, foundBookOne.getPrice());
        
        BookVO foundBookFive = content.get(4);
        
        assertNotNull(foundBookFive.getKey());
        assertNotNull(foundBookFive.getTitle());
        assertNotNull(foundBookFive.getAuthor());
        assertNotNull(foundBookFive.getPrice());
        assertTrue(foundBookFive.getKey() > 0);
        assertEquals("Code complete", foundBookFive.getTitle());
        assertEquals("Steve McConnell", foundBookFive.getAuthor());
        assertEquals(58.0, foundBookFive.getPrice());
    }
     
    private void mockBook() {
        book.setTitle("Docker Deep Dive");
        book.setAuthor("Nigel Poulton");
        book.setPrice(55.99);
        book.setLaunchDate(new Date());
    }
}