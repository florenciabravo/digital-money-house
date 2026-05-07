package com.digitalmoneyhouse.accountservice.api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AccountPatchApiTest extends BaseApiTest {

    private static final String ACCOUNT_SERVICE_URL = "http://localhost:8081";

    /**
     * TC73 - Update alias successfully
     */
    @Test
    void shouldUpdateAliasSuccessfully() {
        String uniqueAlias = "alias.test." + System.currentTimeMillis();

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(Map.of("alias", uniqueAlias))
        .when()
                .patch("/accounts/{id}", accountId)
        .then()
                .statusCode(200)
                .body("alias", equalTo(uniqueAlias));
    }

    /**
     * TC74 - Update alias without token - 401 handled by gateway
     */
    @Test
    void shouldReturn401WithoutToken() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of("alias", "nuevo.alias.test"))
        .when()
                .patch("/accounts/{id}", accountId)
        .then()
                .statusCode(401);
    }

    /**
     * TC75 - Update alias with invalid token - 401 handled by gateway
     */
    @Test
    void shouldReturn401WithInvalidToken() {
        given()
                .header("Authorization", "Bearer token_invalido")
                .contentType(ContentType.JSON)
                .body(Map.of("alias", "nuevo.alias.test"))
        .when()
                .patch("/accounts/{id}", accountId)
        .then()
                .statusCode(401);
    }

    /**
     * TC76 - Update another user's account
     * Direct to account-service (8081): gateway overwrites X-User-Id,
     * so this check can only be tested bypassing the gateway.
     * accountId=1 belongs to test@test.com (userId=1), X-User-Id=2 simulates other@test.com
     */
    @Test
    void shouldReturn403WhenUpdatingAnotherUserAccount() {
        given()
                .baseUri(ACCOUNT_SERVICE_URL)
                .header("X-User-Id", 2)
                .contentType(ContentType.JSON)
                .body(Map.of("alias", "hacker.alias.test"))
        .when()
                .patch("/accounts/{id}", accountId)
        .then()
                .statusCode(403);
    }

    /**
     * TC77 - Alias already in use
     * 'test.alias' belongs to account 1 (test@test.com), seeded in data-integration.sql
     * other@test.com tries to set the same alias on account 2 → 409
     */
    @Test
    void shouldReturn409WhenAliasAlreadyInUse() {
        String otherToken = loginAs("other@test.com", "123456");

        given()
                .header("Authorization", "Bearer " + otherToken)
                .contentType(ContentType.JSON)
                .body(Map.of("alias", "test.alias"))
        .when()
                .patch("/accounts/{id}", 2)
        .then()
                .statusCode(409);
    }

    /**
     * TC78 - Update alias with missing field
     * Direct to account-service (8081): bypasses gateway JWT validation.
     */
    @Test
    void shouldReturn400WhenAliasIsMissing() {
        given()
                .baseUri(ACCOUNT_SERVICE_URL)
                .header("X-User-Id", 1)
                .contentType(ContentType.JSON)
                .body(Map.of())
        .when()
                .patch("/accounts/{id}", accountId)
        .then()
                .statusCode(400);
    }
}
