package com.digitalmoneyhouse.cardservice.api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class CardApiTest extends BaseApiTest {

    /**
     * TC27 - Create card successfully
     */
    @Test
    void shouldCreateCardSuccessfully() {
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(validCardBody())
        .when()
                .post("/cards")
        .then()
                .statusCode(201);
    }

    /**
     * TC28 - Create card without token
     */
    @Test
    void shouldReturn401WhenCreatingCardWithoutToken() {
        given()
                .contentType(ContentType.JSON)
                .body(validCardBody())
        .when()
                .post("/cards")
        .then()
                .statusCode(401);
    }

    /**
     * TC29 - Create card with missing required fields (number and expiryMonth/Year absent)
     */
    @Test
    void shouldReturn400WhenCreatingCardWithMissingFields() {
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "holderName", "Test User",
                        "type", "DEBIT"
                ))
        .when()
                .post("/cards")
        .then()
                .statusCode(400);
    }

    /**
     * TC30 - Create card with invalid card number format
     */
    @Test
    void shouldReturn400WhenCardNumberIsInvalid() {
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "number", "INVALID-NUMBER",
                        "holderName", "Test User",
                        "expiryMonth", 12,
                        "expiryYear", 28,
                        "type", "CREDIT"
                ))
        .when()
                .post("/cards")
        .then()
                .statusCode(400);
    }

    /**
     * TC31 - Internal server error on card creation (500)
     * Requires simulating a DB failure. Commented out for manual isolated execution.
     */
    // @Test
    // void shouldReturn500WhenInternalErrorOnCardCreation() {
    //     given()
    //             .header("Authorization", "Bearer " + token)
    //             .contentType(ContentType.JSON)
    //             .body(validCardBody())
    //     .when()
    //             .post("/cards")
    //     .then()
    //             .statusCode(500);
    // }
}