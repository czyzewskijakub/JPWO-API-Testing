package pl.ioad;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static pl.ioad.utils.Credentials.ADMIN;
import static pl.ioad.utils.Credentials.CUSTOMER;
import static pl.ioad.utils.UniqueNameGenerator.generateNewString;

public class ContactUpdateTest {
    private String id;
    @Before
    public void setUp() {
        RestAssured.baseURI = "http://localhost:8091";
        var accessToken = AuthHelper.getAccessToken(CUSTOMER);
        String random = generateNewString();
        String requestBody = "{\"name\": \"" + "Random" + "\", \"subject\": \"" + random + "\", \"message\": \"" + random +"\"}";
        Response post = given()
                .body(requestBody)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .when()
                .post(CONTACT_ENDPOINT);

        id = post.jsonPath().getString("id");
    }

    private static final String CONTACT_ENDPOINT = "/messages";

    @Test
    public void testUpdateStatusPositive(){
        var accessToken = AuthHelper.getAccessToken(ADMIN);
        String body="{\"status\": \"IN_PROGRESS\"}";
        Response response = given()
                .body(body)
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .put(CONTACT_ENDPOINT + "/" + id + "/status");
        response
                .then()
                .assertThat()
                .statusCode(HTTP_OK)
                .body("success", equalTo(true))
                .header("Content-Type", containsString(ContentType.JSON.toString()))
                .header("Server", not(emptyString()));
    }
@Test
    public void testUpdateStatusWrongId(){
    var accessToken = AuthHelper.getAccessToken(ADMIN);
    String body="{\"status\": \"IN_PROGRESS\"}";
    Response response = given()
            .body(body)
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + accessToken)
            .when()
            .put(CONTACT_ENDPOINT+"/wrong_id/status");
    response
            .then()
            .assertThat()
            .statusCode(HTTP_OK)
            .body("success", equalTo(false))
            .header("Content-Type", containsString(ContentType.JSON.toString()))
            .header("Server", not(emptyString()));
}
@Test
    public void testUpdateStatusWithoutAuthorization(){
    String body="{\"status\": \"RESOLVED\"}";
    Response response = given()
            .body(body)
            .contentType(ContentType.JSON)
            .when()
            .put(CONTACT_ENDPOINT+"/"+id+"/status");
    response
            .then()
            .assertThat()
            .statusCode(HTTP_UNAUTHORIZED)
            .body("message",equalTo("Unauthorized"))
            .header("Content-Type", containsString(ContentType.JSON.toString()))
            .header("Server", not(emptyString()));
}
@Test
    public void testUpdateStatusEmptyBody(){
    var accessToken = AuthHelper.getAccessToken(ADMIN);
    String body="{}";
    Response response = given()
            .body(body)
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + accessToken)
            .when()
            .put(CONTACT_ENDPOINT+"/"+id+"/status");
    response
            .then()
            .assertThat()
            .statusCode(500)
            .header("Server", not(emptyString()));
}

}

