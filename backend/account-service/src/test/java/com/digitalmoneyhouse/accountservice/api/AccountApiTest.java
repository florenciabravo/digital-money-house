package com.digitalmoneyhouse.accountservice.api;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

import static org.hamcrest.Matchers.*;

public class AccountApiTest extends BaseApiTest{
    /**
     * Sprint 2: TC01 - Get balance successfully
     */
    @Test
    void shouldReturnBalanceSuccessfully() {
        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get("/accounts/{id}", accountId)
        .then()
                .statusCode(200)
                .body("balance", notNullValue());
    }

    /**
     * Sprint 2: TC02 - Get balance without token
     */
    @Test
    void shouldReturn401WithoutToken() {
        given()
        .when()
                .get("/accounts/{id}", accountId)
        .then()
                .statusCode(401);
    }

    /**
     * Sprint 2: TC03 - Get balance with invalid token
     */
    @Test
    void shouldReturn401WithInvalidToken() {
        given()
                .header("Authorization", "Bearer token_invalido")
        .when()
                .get("/accounts/{id}", accountId)
        .then()
                .statusCode(401);
    }

    /**
     * Sprint 2: TC04 - Get balance with non-existent ID
     */
    @Test
    void shouldReturn404WhenAccountNotFound() {
        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get("/accounts/{id}", 9999)
        .then()
                .statusCode(404);
    }

    /**
     * Sprint 2: TC05 - Get balance with invalid ID (string)
     */
    @Test
    void shouldReturn400WhenAccountIdIsInvalid() {
        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get("/accounts/{id}", "abc")
        .then()
                .statusCode(400);
    }

    /**
     * Sprint 2: TC06 - User accessing another user's account
     */
    @Test
    void shouldReturn403WhenAccessingOtherUserAccount() {
        String otherToken = loginAs("other@test.com", "123456");

        given()
                .header("Authorization", "Bearer " + otherToken)
        .when()
                .get("/accounts/{id}", accountId) // accountId = 1
        .then()
                .statusCode(403);
    }
}
