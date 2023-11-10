package pl.ioad.user;

import org.junit.Test;
import pl.ioad.AuthHelper;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static pl.ioad.utils.Credentials.CUSTOMER;

public class UserChangePasswordTest {

    private static final String BASE_URL = "http://localhost:8091";
    private static final String USERS_ENDPOINT = "/users";
    private static final String CHANGE_PASSWORD_ENDPOINT = USERS_ENDPOINT + "/change-password";
    private static final String NEW_PASSWORD = "new-password";

    private static final ChangePassword changeToNewPassword = new ChangePassword(
            CUSTOMER.password(),
            NEW_PASSWORD,
            NEW_PASSWORD
    );

    private static final ChangePassword changeToOldPassword = new ChangePassword(
            NEW_PASSWORD,
            CUSTOMER.password(),
            CUSTOMER.password()
    );

    @Test
    public void testChangePasswordSuccess() {
        var accessToken = AuthHelper.getAccessToken(CUSTOMER);

        given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .body(changeToNewPassword)
                .when()
                .post(CHANGE_PASSWORD_ENDPOINT)
                .then()
                .statusCode(HTTP_OK);

        given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .body(changeToOldPassword)
                .when()
                .post(CHANGE_PASSWORD_ENDPOINT)
                .then()
                .statusCode(HTTP_OK);
    }

    @Test
    public void testChangePasswordWrongPassword() {
        var accessToken = AuthHelper.getAccessToken(CUSTOMER);

        var changeWrongPassword = new ChangePassword(
                "wrong-password",
                NEW_PASSWORD,
                NEW_PASSWORD
        );

        given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .body(changeWrongPassword)
                .when()
                .post(CHANGE_PASSWORD_ENDPOINT)
                .then()
                .statusCode(HTTP_BAD_REQUEST)
                .body("success", equalTo(false),
                        "message", equalTo("Your current password does not matches with the password."));
    }

    @Test
    public void testChangePasswordUnauthorized() {
        given()
                .baseUri(BASE_URL)
                .header("Content-Type", "application/json")
                .body(changeToNewPassword)
                .when()
                .post(CHANGE_PASSWORD_ENDPOINT)
                .then()
                .statusCode(HTTP_UNAUTHORIZED)
                .body("message", equalTo("Unauthorized"));
    }

    @Test
    public void testChangePasswordForTheSame() {
        var accessToken = AuthHelper.getAccessToken(CUSTOMER);

        var changeTheSamePassword = new ChangePassword(
                CUSTOMER.password(),
                CUSTOMER.password(),
                CUSTOMER.password()
        );

        given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .body(changeTheSamePassword)
                .when()
                .post(CHANGE_PASSWORD_ENDPOINT)
                .then()
                .statusCode(HTTP_BAD_REQUEST)
                .body("success", equalTo(false),
                        "message", equalTo("New Password cannot be same as your current password."));
    }

    @Test
    public void testChangePasswordWrongConfirmation() {
        var accessToken = AuthHelper.getAccessToken(CUSTOMER);

        var changeWrongPassword = new ChangePassword(
                CUSTOMER.password(),
                NEW_PASSWORD,
                "test"
        );

        given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .body(changeWrongPassword)
                .when()
                .post(CHANGE_PASSWORD_ENDPOINT)
                .then()
                .statusCode(302); // TODO: Why? :)
    }
}

