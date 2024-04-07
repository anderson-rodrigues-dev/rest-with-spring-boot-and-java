package com.example.integrationtests.controller.withxml;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;

import com.example.configs.TestConfigs;

import com.example.integrationtests.testcontainers.AbstractIntegrationTest;
import com.example.integrationtests.vo.AccountCredentialsVO;
import com.example.integrationtests.vo.BookVO;
import com.example.integrationtests.vo.TokenVO;
import com.example.integrationtests.vo.pagedmodels.PagedModelBook;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.DeserializationFeature;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class BookControllerXmlTest extends AbstractIntegrationTest {
	
	private static RequestSpecification specification;
	private static XmlMapper objectMapper;

	private static BookVO book;
	
	@BeforeAll
	public static void setup() {
		objectMapper = new XmlMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		
		book = new BookVO();
	}
	
	@Test
	@Order(1)
	public void authorization() {
		
		AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");
		
		var accessToken = given()
				.basePath("/auth/signin")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_XML)
					.accept(TestConfigs.CONTENT_TYPE_XML)
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
				.setBasePath("/api/book/v1")
				.setPort(TestConfigs.SERVER_PORT)
					.addFilter(new RequestLoggingFilter(LogDetail.ALL))
					.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
	}
	
	@Test
	@Order(2)
	public void testCreate() throws JsonProcessingException {
		mockBook();
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
					.body(book)
					.when()
					.post()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
        book = objectMapper.readValue(content, BookVO.class);
        
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
	public void testUpdate() throws JsonProcessingException {

        book.setTitle("Docker Deep Dive - Updated");
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
					.body(book)
					.when()
					.post()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
        BookVO bookUpdated = objectMapper.readValue(content, BookVO.class);
        
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
	public void testFindById() throws JsonProcessingException {
		mockBook();
			
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
					.pathParam("id", book.getId())
					.when()
					.get("{id}")
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		BookVO foundBook = objectMapper.readValue(content, BookVO.class);
        
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

		given().spec(specification)
			.contentType(TestConfigs.CONTENT_TYPE_XML)
			.accept(TestConfigs.CONTENT_TYPE_XML)
				.pathParam("id", book.getId())
				.when()
				.delete("{id}")
			.then()
				.statusCode(204);
	}
	
	@Test
	@Order(6)
	public void testFindAll() throws JsonProcessingException {
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
					.when()
					.get()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();

		PagedModelBook wrapper = objectMapper.readValue(content, PagedModelBook.class);

		List<BookVO> books = wrapper.getContent();

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
	public void testFindAllWithoutToken() {
		
		RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
			.setBasePath("/api/book/v1")
			.setPort(TestConfigs.SERVER_PORT)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
			.build();
		
		given().spec(specificationWithoutToken)
			.contentType(TestConfigs.CONTENT_TYPE_XML)
			.accept(TestConfigs.CONTENT_TYPE_XML)
				.when()
				.get()
			.then()
				.statusCode(403);
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
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
				.queryParams("page", 1, "limit", 10, "direction", "asc")
				.when()
				.get()
				.then()
				.statusCode(200)
				.extract()
				.body()
				.asString();


		assertNotNull(content);

		assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/api/book/v1/10</href></links>"));
		assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/api/book/v1/1</href></links>"));
		assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/api/book/v1/14</href></links>"));
		assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/api/book/v1/6</href></links>"));

		assertTrue(content.contains("<links><rel>first</rel><href>http://localhost:8888/api/book/v1?limit=10&amp;direction=title%3A%20ASC&amp;page=0&amp;size=10&amp;sort=title,asc</href></links>"));
		assertTrue(content.contains("<links><rel>prev</rel><href>http://localhost:8888/api/book/v1?limit=10&amp;direction=title%3A%20ASC&amp;page=0&amp;size=10&amp;sort=title,asc</href></links>"));
		assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/api/book/v1?page=1&amp;limit=10&amp;direction=title%3A%20ASC</href></links>"));
		assertTrue(content.contains("<links><rel>last</rel><href>http://localhost:8888/api/book/v1?limit=10&amp;direction=title%3A%20ASC&amp;page=1&amp;size=10&amp;sort=title,asc</href></links>"));

		assertTrue(content.contains("<page><size>10</size><totalElements>15</totalElements><totalPages>2</totalPages><number>1</number></page>"));
	}
	
    private void mockBook() {
        book.setTitle("Docker Deep Dive");
        book.setAuthor("Nigel Poulton");
        book.setPrice(55.99);
        book.setLaunchDate(new Date());
    }  
}