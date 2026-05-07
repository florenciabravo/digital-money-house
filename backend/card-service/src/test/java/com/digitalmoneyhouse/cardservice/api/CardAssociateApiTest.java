package com.digitalmoneyhouse.cardservice.api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class CardAssociateApiTest extends BaseApiTest {

    private Map<String, Object> validAssociateBody() {
        return Map.of("cardId", cardId);
    }

    /**
     * TC32 - Associate card to account successfully
     * Real path: POST /cards/accounts/{accountId}/cards
     */
    @Test
    void shouldAssociateCardSuccessfully() {

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
    }

    /**
     * TC33 - Associate card without token
     */
    @Test
    void shouldReturn401WhenAssociatingCardWithoutToken() {
        given()
                .contentType(ContentType.JSON)
                .body(validAssociateBody())
        .when()
                .post("/cards/accounts/{accountId}/cards", accountId)
        .then()
                .statusCode(401);
    }

    /**
     * TC34 - Associate card to non-existent account
     */
    @Test
    void shouldReturn404WhenAccountNotFound() {
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(validAssociateBody())
        .when()
                .post("/cards/accounts/{accountId}/cards", 9999)
        .then()
                .statusCode(404);
    }

    /**
     * TC35 - Associate card that is already linked to another account
     */
    @Test
    void shouldReturn409WhenCardAlreadyAssociated() {

        // user1 create card
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

        // user1 links it to account 1
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(Map.of("cardId", newCardId))
        .when()
                .post("/cards/accounts/{accountId}/cards", 1)
        .then()
                .statusCode(201);

        // login user2
        String otherToken = loginAs("other@test.com", "123456");

        // user2 tries to link the same card to their account 2 -> 409
        given()
                .header("Authorization", "Bearer " + otherToken)
                .contentType(ContentType.JSON)
                .body(Map.of("cardId", newCardId))
        .when()
                .post("/cards/accounts/{accountId}/cards", 2)
        .then()
                .statusCode(409);
    }

    /**
     * TC36 - Associate card with invalid account ID (string)
     */
    @Test
    void shouldReturn400WhenAccountIdIsInvalid() {
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(validAssociateBody())
        .when()
                .post("/cards/accounts/{accountId}/cards", "abc")
        .then()
                .statusCode(400);
    }

    /**
     * TC37 - Associate card belonging to another user
     * cardId=1 belongs to test@test.com; other@test.com should get 403
     */
    @Test
    void shouldReturn403WhenAssociatingCardOfAnotherUser() {
        String otherToken = loginAs("other@test.com", "123456");

        given()
                .header("Authorization", "Bearer " + otherToken)
                .contentType(ContentType.JSON)
                .body(validAssociateBody())
        .when()
                .post("/cards/accounts/{accountId}/cards", accountId)
        .then()
                .statusCode(403);
    }

    /**
     * TC38 - Internal server error on card association (500)
     * Requires simulating a DB failure. Commented out for manual isolated execution.
     */
    // @Test
    // void shouldReturn500WhenInternalErrorOnAssociate() {
    //     given()
    //             .header("Authorization", "Bearer " + token)
    //             .contentType(ContentType.JSON)
    //             .body(validAssociateBody())
    //     .when()
    //             .post("/cards/accounts/{accountId}/cards", accountId)
    //     .then()
    //             .statusCode(500);
    // }
}
