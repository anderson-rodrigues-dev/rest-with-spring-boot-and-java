package com.example.integrationtests.swagger;

import com.example.configs.TestConfigs;
import com.example.integrationtests.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SwaggerIntegrationTest extends AbstractIntegrationTest {
    @Test
    void shouldDisplaySwaggerUIPage(){
        var content =
            given()
                    .basePath("/swagger-ui/index.html")
                    .port(TestConfigs.SERVER_PORT)
                    .when()
                        .get()
                    .then()
                        .statusCode(200)
                    .extract()
                        .body()
                            .asString();

        assertTrue(content.contains("Swagger UI"));
    }
}
