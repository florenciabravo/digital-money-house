package com.digitalmoneyhouse.cardservice.api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CardListApiTest extends BaseApiTest{

    @BeforeAll
    void setupCardForList() {
        // 1. Create a new card
        Long newCardId =
                given()
                        .header("Authorization", "Bearer " + token)
                        .contentType(ContentType.JSON)
                        .body(validCardBody())
                .when()
                        .post("/cards")
                .then()
                        .statusCode(201)
                        .extract()
                        .jsonPath()
                        .getLong("id");

        // 2. Link it to the account
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(Map.of("cardId", newCardId))
        .when()
                .post("/cards/accounts/{accountId}/cards", accountId)
        .then()
                .statusCode(201);
    }

    /**
     * TC39 - Get cards for account successfully
     * Real path: GET /cards/accounts/{accountId}/cards
     */
    @Test
    void shouldReturnCardsSuccessfully() {
        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get("/cards/accounts/{accountId}/cards", accountId)
        .then()
                .statusCode(200)
                .body("$", instanceOf(java.util.List.class))
                .body("$", not(empty()))
                .body("[0].number", notNullValue())
                .body("[0].type", notNullValue());
    }

    /**
     * TC40 - Get cards without token
     */
    @Test
    void shouldReturn401WithoutToken() {
        given()
        .when()
                .get("/cards/accounts/{accountId}/cards", accountId)
        .then()
                .statusCode(401);
    }

    /**
     * TC41 - Get cards with invalid token
     */
    @Test
    void shouldReturn401WithInvalidToken() {
        given()
                .header("Authorization", "Bearer token_invalido")
        .when()
                .get("/cards/accounts/{accountId}/cards", accountId)
        .then()
                .statusCode(401);
    }

    /**
     * TC42 - Account with no associated cards returns empty list
     * other@test.com has accountId=2 with no cards seeded
     */
    @Test
    void shouldReturnEmptyListWhenNoCardsAssociated() {
        String otherToken = loginAs("other@test.com", "123456");

        given()
                .header("Authorization", "Bearer " + otherToken)
        .when()
                .get("/cards/accounts/{accountId}/cards", 2)
        .then()
                .statusCode(200)
                .body("$", empty());
    }

    /**
     * TC43 - Get cards with invalid account ID (string)
     */
    @Test
    void shouldReturn400WhenAccountIdIsInvalid() {
        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get("/cards/accounts/{accountId}/cards", "abc")
        .then()
                .statusCode(400);
    }

    /**
     * TC44 - User accesses cards from another user's account
     * other@test.com tries to access accountId=1 (owned by test@test.com)
     */
    @Test
    void shouldReturn403WhenAccessingAnotherUserCards() {
        String otherToken = loginAs("other@test.com", "123456");

        given()
                .header("Authorization", "Bearer " + otherToken)
        .when()
                .get("/cards/accounts/{accountId}/cards", accountId)
        .then()
                .statusCode(403);
    }

    /**
     * TC45 - Get cards for non-existent account
     */
    @Test
    void shouldReturn404WhenAccountNotFound() {
        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get("/cards/accounts/{accountId}/cards", 9999)
        .then()
                .statusCode(404);
    }

    /**
     * TC46 - Internal server error on list cards (500)
     * Requires simulating a DB failure. Commented out for manual isolated execution.
     */
    // @Test
    // void shouldReturn500WhenInternalErrorOnListCards() {
    //     given()
    //             .header("Authorization", "Bearer " + token)
    //     .when()
    //             .get("/cards/accounts/{accountId}/cards", accountId)
    //     .then()
    //             .statusCode(500);
    // }
}
