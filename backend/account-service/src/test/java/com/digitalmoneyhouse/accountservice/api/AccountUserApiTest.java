package com.digitalmoneyhouse.accountservice.api;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AccountUserApiTest extends BaseApiTest {

    private static final String ACCOUNT_SERVICE_URL = "http://localhost:8081";

    /**
     * Sprint 2: TC22 - Get account by userId successfully
     */
    @Test
    void shouldReturnAccountByUserIdSuccessfully() {
        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get("/accounts/user/{userId}", 1)
        .then()
                .statusCode(200)
                .body("cvu", notNullValue())
                .body("alias", notNullValue());
    }

    /**
     * Sprint 2: TC23 - Access another user's account
     * other@test.com has userId=2, tries to access userId=1
     */
    @Test
    void shouldReturn403WhenAccessingOtherUserAccount() {
        String otherToken = loginAs("other@test.com", "123456");

        given()
                .header("Authorization", "Bearer " + otherToken)
        .when()
                .get("/accounts/user/{userId}", 1)
        .then()
                .statusCode(403);
    }

    /**
     * Sprint 2: TC24 - userId with no associated account
     * Direct to account-service: bypasses gateway so X-User-Id matches path.
     */
    @Test
    void shouldReturn404WhenAccountNotFoundForUser() {
        given()
                .baseUri(ACCOUNT_SERVICE_URL)
                //.header("Authorization", "Bearer " + token)
                .header("X-User-Id", 9999)
        .when()
                .get("/accounts/user/{userId}", 9999)
        .then()
                .statusCode(404);
    }

    /**
     * Sprint 2: TC25 - Invalid userId (string)
     */
    @Test
    void shouldReturn400WhenUserIdIsInvalid() {
        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get("/accounts/user/{userId}", "abc")
        .then()
                .statusCode(400);
    }

    /**
     * Sprint 2: TC26 - Internal error in account-service (DB not responding → 500)
     * Requires simulating DB failure manually.
     * Run in isolation.
     */
    // @Test
    // void shouldReturn500WhenAccountServiceHasInternalError() {
    //     given()
    //             .header("Authorization", "Bearer " + token)
    //     .when()
    //             .get("/accounts/user/{userId}", 1)
    //     .then()
    //             .statusCode(500);
    // }

}
