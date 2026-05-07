package com.digitalmoneyhouse.userservice.api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserPatchApiTest extends BaseApiTest {

    private static final String USER_SERVICE_URL = "http://localhost:8084";

    /**
     * TC68 - Update user profile successfully
     */
    @Test
    void shouldUpdateUserProfileSuccessfully() {
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "firstName", "Updated",
                        "lastName", "Name"
                ))
        .when()
                .patch("/users/{id}", 1)
        .then()
                .statusCode(200)
                .body("firstName", equalTo("Updated"))
                .body("lastName", equalTo("Name"));
    }

    /**
     * TC69 - Update profile without token - 401 handled by gateway
     */
    @Test
    void shouldReturn401WithoutToken() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "firstName", "Updated",
                        "lastName", "Name"
                ))
        .when()
                .patch("/users/{id}", 1)
        .then()
                .statusCode(401);
    }

    /**
     * TC70 - Update profile with invalid token - 401 handled by gateway
     */
    @Test
    void shouldReturn401WithInvalidToken() {
        given()
                .header("Authorization", "Bearer token_invalido")
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "firstName", "Updated",
                        "lastName", "Name"
                ))
        .when()
                .patch("/users/{id}", 1)
        .then()
                .statusCode(401);
    }

    /**
     * TC71 - Update another user's profile
     * Direct to user-service (8084): gateway overwrites X-User-Id,
     * so this check can only be tested bypassing the gateway.
     */
    @Test
    void shouldReturn403WhenUpdatingAnotherUserProfile() {
        given()
                .baseUri(USER_SERVICE_URL)
                .header("X-User-Id", 2)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "firstName", "Hacker",
                        "lastName", "Attempt"
                ))
        .when()
                .patch("/users/{id}", 1)
        .then()
                .statusCode(403);
    }

    /**
     * TC72 - Update profile with missing required fields
     * Direct to user-service (8084): bypasses gateway JWT validation.
     */
    @Test
    void shouldReturn400WhenFieldsAreMissing() {
        given()
                .baseUri(USER_SERVICE_URL)
                .header("X-User-Id", 1)
                .contentType(ContentType.JSON)
                .body(Map.of())
        .when()
                .patch("/users/{id}", 1)
        .then()
                .statusCode(400);
    }
}


