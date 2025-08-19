import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class AuthIntegrationTest {

    @BeforeAll
    static  void  setUp(){
        RestAssured.baseURI = "http://localhost:4004";
    }

    @Test
    public void shouldReturnOkWithValidToken(){

        String loginPayload = """
                {
                "email" : "testuser@test.com",
                "password" :"password123"
                }
                """;

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(loginPayload)
                .when()
                .post("auth/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .extract().response();
        System.out.println("Generated Token: " + response.jsonPath().getString("token"));
    }

    @Test
    public void shouldReturnBadRequestWithInvalidToken(){

        String payload = """
                {
                "email": "test",
                "password": "password123"
        """;

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("auth/login")
                .then()
                .statusCode(400)
                .body("token",nullValue())
                .extract().response();
        System.out.println("Generated Token: " + response.jsonPath().getString("token"));
    }
    }
