package com.digitalmoneyhouse.accountservice.api;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class TransactionApiTest extends BaseApiTest {
    /**
     * Sprint 2: TC07 - Get transactions successfully
     */
    @Test
    void shouldReturnTransactionsSuccessfully() {
        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get("/accounts/{id}/transactions", accountId)
        .then()
                .statusCode(200)
                .body("$", instanceOf(java.util.List.class))
                .body("$", not(empty()))
                .body("[0].createdAt", notNullValue()); // The first element is the most recent
    }

    /**
     * Sprint 2: TC08 - Get transactions without token
     */
    @Test
    void shouldReturn401WithoutToken() {
        given()
        .when()
                .get("/accounts/{id}/transactions", accountId)
        .then()
                .statusCode(401);
    }

    /**
     * Sprint 2: TC09 - Get transactions with invalid token
     */
    @Test
    void shouldReturn401WithInvalidToken() {
        given()
                .header("Authorization", "Bearer token_invalido")
        .when()
                .get("/accounts/{id}/transactions", accountId)
        .then()
                .statusCode(401);
    }

    /**
     * Sprint 2: TC10 - Get transactions with non-existent account ID
     */
    @Test
    void shouldReturn404WhenAccountNotFound() {
        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get("/accounts/{id}/transactions", 9999)
        .then()
                .statusCode(404);
    }

    /**
     * Sprint 2: TC11 - Get transactions with invalid ID (string)
     */
    @Test
    void shouldReturn400WhenAccountIdIsInvalid() {
        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get("/accounts/{id}/transactions", "abc")
        .then()
                .statusCode(400);
    }

    /**
     * Sprint 2: TC12 - User accessing another user's transactions
     */
    @Test
    void shouldReturn403WhenAccessingOtherUserTransactions() {
        String otherToken = loginAs("other@test.com", "123456");

        given()
                .header("Authorization", "Bearer " + otherToken)
        .when()
                .get("/accounts/{id}/transactions", accountId)
        .then()
                .statusCode(403);
    }

    /**
     * Sprint 2: TC13 - Account with no transactions returns empty list
     */
    @Test
    void shouldReturnEmptyListWhenNoTransactions() {
        String otherToken = loginAs("other@test.com", "123456");
        given()
                .header("Authorization", "Bearer " + otherToken)
        .when()
                .get("/accounts/{id}/transactions", 2) // account with no transactions
        .then()
                .statusCode(200)
                .body("$", empty());
    }

    /**
     * Sprint 2: TC14 - Validate limit=5 returns only 5 elements
     */
    @Test
    void shouldReturnOnlyFiveTransactionsWhenLimitIsSet() {
        given()
                .header("Authorization", "Bearer " + token)
                .queryParam("limit", 5)
        .when()
                .get("/accounts/{id}/transactions", accountId)
        .then()
                .statusCode(200)
                .body("$", hasSize(5));
    }
}
