package com.digitalmoneyhouse.userservice.api;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserApiTest extends BaseApiTest {

    private static final String USER_SERVICE_URL = "http://localhost:8084";

    /**
     * Sprint 2: TC15 - Get profile successfully
     */
    @Test
    void shouldReturnUserProfileSuccessfully() {
        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get("/users/{id}", 1)
        .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("firstName", notNullValue())
                .body("lastName", notNullValue())
                .body("cvu", notNullValue())
                .body("alias", notNullValue());
    }

    /**
     * Sprint 2: TC16 - Get profile without token - 401 handled by gateway
     */
    @Test
    void shouldReturn401WithoutToken() {
        given()
        .when()
                .get("/users/{id}", 1)
        .then()
                .statusCode(401);
    }

    /**
     * Sprint 2: TC17 - Get profile with invalid token - 401 handled by gateway
     */
    @Test
    void shouldReturn401WithInvalidToken() {
        given()
                .header("Authorization", "Bearer token_invalido")
        .when()
                .get("/users/{id}", 1)
        .then()
                .statusCode(401);
    }

    /**
     * Sprint 2: TC18 - Access another user's profile
     * other@test.com has id=2, tries to access user id=1
     * Direct to user-service (8084): gateway overwrites X-User-Id,
     * so this check can only be tested bypassing the gateway.
     */
    @Test
    void shouldReturn403WhenAccessingOtherUserProfile() {
        //String otherToken = loginAs("other@test.com", "123456");

        given()
                .baseUri(USER_SERVICE_URL)
                //.header("Authorization", "Bearer " + otherToken)
                .header("X-User-Id", 2)
        .when()
                .get("/users/{id}", 1)
        .then()
                .statusCode(403);
    }

    /**
     * Sprint 2: TC19 - Non-existent user ID
     * Direct to user-service (8084): bypasses gateway JWT validation.
     */
    @Test
    void shouldReturn404WhenUserNotFound() {
        given()
                .baseUri(USER_SERVICE_URL)
                //.header("Authorization", "Bearer " + token)
                .header("X-User-Id", 9999)
        .when()
                .get("/users/{id}", 9999)
        .then()
                .statusCode(404);
    }

    /**
     * Sprint 2: TC20 - Invalid user ID (string)
     * Direct to user-service (8084): bypasses gateway JWT validation.
     */
    @Test
    void shouldReturn400WhenUserIdIsInvalid() {
        given()
                .baseUri(USER_SERVICE_URL)
                //.header("Authorization", "Bearer " + token)
        .when()
                .get("/users/{id}", "abc")
        .then()
                .statusCode(400);
    }

    /**
     * Sprint 2: TC21 - account-service down → 503
     * Requires stopping account-service manually before running.
     * Run in isolation: docker stop backend-account-service-1
     */
    // @Test
    // void shouldReturn503WhenAccountServiceIsDown() {
    //     given()
    //             .header("Authorization", "Bearer " + token)
    //     .when()
    //             .get("/users/{id}", 1)
    //     .then()
    //             .statusCode(503);
    // }

}
