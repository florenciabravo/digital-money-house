package com.digitalmoneyhouse.cardservice.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static io.restassured.RestAssured.given;

@ActiveProfiles("integration")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseApiTest {

    protected String token;
    protected Long accountId = 1L;
    protected Long cardId = 1L;

    protected String email = "test@test.com";
    protected String password = "123456";

    protected static final String BASE_URL = "http://localhost:8083";

    @BeforeAll
    void setup() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        token = given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "email", email,
                        "password", password
                ))
        .when()
                .post("/auth/login")
        .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("token");

        System.out.println("TOKEN: " + token);
    }

    protected String loginAs(String email, String password) {
        return given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "email", email,
                        "password", password
                ))
        .when()
                .post("/auth/login")
        .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("token");
    }

    protected Map<String, Object> validCardBody() {
        return Map.of(
                "number", String.valueOf(
                        4000000000000000L + System.currentTimeMillis() % 100000000000000L
                ),
                "holderName", "Test User",
                "expiryMonth", 12,
                "expiryYear", 28,
                "type", "CREDIT"
        );
    }
}