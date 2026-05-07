package com.digitalmoneyhouse.cardservice.api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class CardDeleteApiTest extends BaseApiTest{

    @BeforeAll
    void setupCardForDelete() {
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

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(Map.of("cardId", newCardId))
        .when()
                .post("/cards/accounts/{accountId}/cards", accountId)
        .then()
                .statusCode(201);

        cardId = newCardId;
    }

    /**
     * TC57 - Delete card successfully
     * Real path: DELETE /cards/accounts/{accountId}/cards/{cardId}
     */
    @Test
    void shouldDeleteCardSuccessfully() {
        // Create your own card for this test
        Long cardToDelete =
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

        // Link it
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(Map.of("cardId", cardToDelete))
        .when()
                .post("/cards/accounts/{accountId}/cards", accountId)
        .then()
                .statusCode(201);

        // Delete it
        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .delete("/cards/accounts/{accountId}/cards/{cardId}", accountId, cardToDelete)
        .then()
                .statusCode(200);
    }

    /**
     * TC58 - Delete card without token
     */
    @Test
    void shouldReturn401WithoutToken() {
        given()
        .when()
                .delete("/cards/accounts/{accountId}/cards/{cardId}", accountId, cardId)
        .then()
                .statusCode(401);
    }

    /**
     * TC59 - Delete card with invalid token
     */
    @Test
    void shouldReturn401WithInvalidToken() {
        given()
                .header("Authorization", "Bearer token_invalido")
        .when()
                .delete("/cards/accounts/{accountId}/cards/{cardId}", accountId, cardId)
        .then()
                .statusCode(401);
    }

    /**
     * TC60 - Delete card with invalid account ID (string)
     */
    @Test
    void shouldReturn400WhenAccountIdIsInvalid() {
        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .delete("/cards/accounts/{accountId}/cards/{cardId}", "abc", cardId)
        .then()
                .statusCode(400);
    }

    /**
     * TC61 - Delete card with invalid card ID (string)
     */
    @Test
    void shouldReturn400WhenCardIdIsInvalid() {
        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .delete("/cards/accounts/{accountId}/cards/{cardId}", accountId, "abc")
        .then()
                .statusCode(400);
    }

    /**
     * TC62 - Delete card for non-existent account
     */
    @Test
    void shouldReturn404WhenAccountNotFound() {
        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .delete("/cards/accounts/{accountId}/cards/{cardId}", 9999, cardId)
        .then()
                .statusCode(404);
    }

    /**
     * TC63 - Delete non-existent card
     */
    @Test
    void shouldReturn404WhenCardNotFound() {
        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .delete("/cards/accounts/{accountId}/cards/{cardId}", accountId, 9999)
        .then()
                .statusCode(404);
    }

    /**
     * TC64 - Card exists but is not associated to the given account
     * cardId=2 exists but belongs to a different account
     */
    @Test
    void shouldReturn404WhenCardNotAssociatedToAccount() {
        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .delete("/cards/accounts/{accountId}/cards/{cardId}", accountId, 2)
        .then()
                .statusCode(404);
    }

    /**
     * TC65 - User deletes card from another user's account
     * other@test.com tries to delete from accountId=1 (owned by test@test.com)
     */
    @Test
    void shouldReturn403WhenDeletingAnotherUserCard() {
        String otherToken = loginAs("other@test.com", "123456");

        given()
                .header("Authorization", "Bearer " + otherToken)
        .when()
                .delete("/cards/accounts/{accountId}/cards/{cardId}", accountId, cardId)
        .then()
                .statusCode(403);
    }

    /**
     * TC66 - Delete already deleted card
     * Uses cardId=99, assumed to be pre-deleted in data.sql.
     * Alternatively, run after TC57 using the same cardId.
     */
    @Test
    void shouldReturn404WhenDeletingAlreadyDeletedCard() {
        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .delete("/cards/accounts/{accountId}/cards/{cardId}", accountId, 99)
        .then()
                .statusCode(404);
    }

    /**
     * TC67 - Internal server error on card deletion (500)
     * Requires simulating a DB failure. Commented out for manual isolated execution.
     */
    // @Test
    // void shouldReturn500WhenInternalErrorOnDeleteCard() {
    //     given()
    //             .header("Authorization", "Bearer " + token)
    //     .when()
    //             .delete("/cards/accounts/{accountId}/cards/{cardId}", accountId, cardId)
    //     .then()
    //             .statusCode(500);
    // }
}
