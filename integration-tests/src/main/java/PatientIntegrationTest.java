import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.notNullValue;

public class PatientIntegrationTest {

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost:4004";
    }

    @Test
    public void shouldReturnPatientWithValidToken (){
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

        Response ans =RestAssured
                .when()
                .get("/api/patients")
                .then()
                .statusCode(200)
                .body("patients", notNullValue())
                .extract().response();

        System.out.println("Generated Ans Token: " + ans.jsonPath().getString("patients"));
    }
}
