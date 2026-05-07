package com.digitalmoneyhouse.cardservice.api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CardDetailApiTest extends BaseApiTest{

    @BeforeAll
    void setupCardForDetail() {
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

        // 3. Overwrite the cardId inherited from BaseApiTest
        cardId = newCardId;
    }

    /**
     * TC47 - Get card detail successfully
     * Real path: GET /cards/accounts/{accountId}/cards/{cardId}
     */
    @Test
    void shouldReturnCardDetailSuccessfully() {
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/cards/accounts/{accountId}/cards/{cardId}", accountId, cardId)
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("number", notNullValue())
                .body("holderName", notNullValue())
                .body("expiryMonth", notNullValue())
                .body("expiryYear", notNullValue())
                .body("type", notNullValue());
    }

    /**
     * TC48 - Get card detail without token
     */
    @Test
    void shouldReturn401WithoutToken() {
        given()
                .when()
                .get("/cards/accounts/{accountId}/cards/{cardId}", accountId, cardId)
                .then()
                .statusCode(401);
    }

    /**
     * TC49 - Get card detail with invalid token
     */
    @Test
    void shouldReturn401WithInvalidToken() {
        given()
                .header("Authorization", "Bearer token_invalido")
                .when()
                .get("/cards/accounts/{accountId}/cards/{cardId}", accountId, cardId)
                .then()
                .statusCode(401);
    }

    /**
     * TC50 - Get card detail with invalid account ID (string)
     */
    @Test
    void shouldReturn400WhenAccountIdIsInvalid() {
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/cards/accounts/{accountId}/cards/{cardId}", "abc", cardId)
                .then()
                .statusCode(400);
    }

    /**
     * TC51 - Get card detail with invalid card ID (string)
     */
    @Test
    void shouldReturn400WhenCardIdIsInvalid() {
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/cards/accounts/{accountId}/cards/{cardId}", accountId, "abc")
                .then()
                .statusCode(400);
    }

    /**
     * TC52 - Get card detail for non-existent account
     */
    @Test
    void shouldReturn404WhenAccountNotFound() {
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/cards/accounts/{accountId}/cards/{cardId}", 9999, cardId)
                .then()
                .statusCode(404);
    }

    /**
     * TC53 - Get detail of non-existent card
     */
    @Test
    void shouldReturn404WhenCardNotFound() {
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/cards/accounts/{accountId}/cards/{cardId}", accountId, 9999)
                .then()
                .statusCode(404);
    }

    /**
     * TC54 - Card exists but is not associated to the given account
     * cardId=2 exists but belongs to a different account
     */
    @Test
    void shouldReturn404WhenCardNotAssociatedToAccount() {
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/cards/accounts/{accountId}/cards/{cardId}", accountId, 2)
                .then()
                .statusCode(404);
    }

    /**
     * TC55 - User accesses card detail from another user's account
     * other@test.com tries to access accountId=1 (owned by test@test.com)
     */
    @Test
    void shouldReturn403WhenAccessingAnotherUserCardDetail() {
        String otherToken = loginAs("other@test.com", "123456");

        given()
                .header("Authorization", "Bearer " + otherToken)
                .when()
                .get("/cards/accounts/{accountId}/cards/{cardId}", accountId, cardId)
                .then()
                .statusCode(403);
    }

    /**
     * TC56 - Internal server error on card detail (500)
     * Requires simulating a DB failure. Commented out for manual isolated execution.
     */
    // @Test
    // void shouldReturn500WhenInternalErrorOnCardDetail() {
    //     given()
    //             .header("Authorization", "Bearer " + token)
    //     .when()
    //             .get("/cards/accounts/{accountId}/cards/{cardId}", accountId, cardId)
    //     .then()
    //             .statusCode(500);
    // }
}
