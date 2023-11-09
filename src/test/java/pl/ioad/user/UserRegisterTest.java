package pl.ioad.user;

import io.restassured.http.ContentType;
import org.junit.Test;
import pl.ioad.AuthHelper;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static org.hamcrest.CoreMatchers.equalTo;
import static pl.ioad.utils.Credentials.ADMIN;

public class UserRegisterTest {

    private static final String BASE_URL = "https://api.practicesoftwaretesting.com";
    private static final String USERS_ENDPOINT = "/users";
    private static final String REGISTER_ENDPOINT = USERS_ENDPOINT + "/register";

    private static final UserCreate USER = new UserCreate(
            "John",
            "Doe",
            "Street 1",
            "City",
            "State",
            "Country",
            "1234AA",
            "0987654321",
            "1970-01-01",
            "invalid-email@doe.example",
            "super-secret"
    );

    @Test
    public void testRegisterUserSuccessfully() {
        var accessToken = AuthHelper.getAccessToken(ADMIN);

        var registeredId = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .body(USER)
                .when()
                .post(REGISTER_ENDPOINT)
                .then()
                .statusCode(HTTP_CREATED)
                .extract()
                .as(DBUser.class)
                .id();

        given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete(USERS_ENDPOINT + "/" + registeredId)
                .then()
                .statusCode(HTTP_NO_CONTENT);
    }

    @Test
    public void testRegisterUserWithExistingEmail() {

        var invalidUser = USER.clone();
        invalidUser.setEmail("john@doe.example");

        given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .body(invalidUser)
                .when()
                .post(REGISTER_ENDPOINT)
                .then()
                .statusCode(422)
                .body("email[0]", equalTo("A customer with this email address already exists."));
    }

    @Test
    public void testRegisterUserWithMissingEmail() {

        var invalidUser = USER.clone();
        invalidUser.setEmail(null);

        given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .body(invalidUser)
                .when()
                .post(REGISTER_ENDPOINT)
                .then()
                .statusCode(422)
                .body("email[0]", equalTo("The email field is required."));
    }

    @Test
    public void testRegisterUserMissingHeader() {

        var invalidUser = USER.clone();
        invalidUser.setEmail(null);

        given()
                .baseUri(BASE_URL)
                .body(invalidUser)
                .when()
                .post(REGISTER_ENDPOINT)
                .then()
                .statusCode(422)
                .body("first_name[0]", equalTo("The first name field is required."),
                        "last_name[0]", equalTo("The last name field is required."),
                        "email[0]", equalTo("The email field is required."),
                        "password[0]", equalTo("The password field is required."));
    }

}
