package pl.ioad;

import org.junit.Test;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static pl.ioad.models.Credentials.ADMIN;
import static pl.ioad.models.Credentials.CUSTOMER;

public class UserDataFetchTest {

    private static final String BASE_URL = "https://api.practicesoftwaretesting.com";
    private static final String USERS_ENDPOINT = "/users";
    private final AuthHelper authHelper = new AuthHelper();

    @Test
    public void testGetExactNumberOfUsers() {
        var accessToken = authHelper.getAccessToken(ADMIN);

        given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get(USERS_ENDPOINT)
                .then()
                .statusCode(HTTP_OK);
    }

    @Test
    public void testGetUsersUnauthorized() {
        var response = given()
                .baseUri(BASE_URL)
                .when()
                .get(USERS_ENDPOINT);

        response.then().statusCode(HTTP_UNAUTHORIZED);
    }

    @Test
    public void testGetUsersForbidden() {
        var accessToken = authHelper.getAccessToken(CUSTOMER);

        given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get(USERS_ENDPOINT)
                .then()
                .statusCode(HTTP_FORBIDDEN);
    }

    @Test
    public void testSearchForUserByNameSuccess() {
        var accessToken = authHelper.getAccessToken(ADMIN);

        given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get(USERS_ENDPOINT + "?q=Jane")
                .then()
                .statusCode(HTTP_OK)
                .body("data[0].first_name", equalTo("Jane"))
                .and()
                .body("data[0].last_name", equalTo("Doe"));
    }

    @Test
    public void testSearchForUserByNameNotFound() {
        var accessToken = authHelper.getAccessToken(ADMIN);

        given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get(USERS_ENDPOINT + "/search?q=123")
                .then()
                .statusCode(HTTP_OK)
                .body("total", equalTo(0));
    }

    @Test
    public void testSearchForUserByNameUnauthorized() {
        var response = given()
                .baseUri(BASE_URL)
                .when()
                .get(USERS_ENDPOINT + "/search?q=123");

        response.then().statusCode(HTTP_UNAUTHORIZED);
    }
}
