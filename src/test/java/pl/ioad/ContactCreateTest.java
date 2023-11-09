package pl.ioad;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static pl.ioad.utils.JsonValidatorSettings.settings;
import static pl.ioad.utils.UniqueNameGenerator.*;

public class ContactCreateTest {
    private static final String CONTACT_ENDPOINT = "/messages";
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://api.practicesoftwaretesting.com";
    }

    @Test
    public void createNewContactPositive(){
        String random = generateNewString();
        String requestBody = "{\"first_name\": \"" + random + "\", \"last_name\": \"" + random + "\", \"email\": \"" + random + "\", \"subject\": \"" + random + "\", \"message\": \"" + random +"\"}";

        Response response = given()
                .body(requestBody)
                .contentType(ContentType.JSON)
                .when()
                .post(CONTACT_ENDPOINT);

        response
                .then()
                .assertThat()
                .statusCode(HTTP_OK)
                .body(matchesJsonSchemaInClasspath("schemas/contact_created.json").using(settings))
                .body("email", equalTo(random))
                .body("subject", equalTo(random))
                .body("message", equalTo(random))
                .header("Content-Type", containsString(ContentType.JSON.toString()))
                .header("Server", not(emptyString()));

        assertThat(response.time(), lessThan(5000L)); // Temporary 5000 ms
    }

    @Test
    public void createNewContactMissingUserData(){
        String random = generateNewString();
        String requestBody = "{\"subject\": \"" + random + "\", \"message\": \"" + random +"\"}";

        Response response = given()
                .body(requestBody)
                .contentType(ContentType.JSON)
                .when()
                .post(CONTACT_ENDPOINT);

        response
                .then()
                .assertThat()
                .statusCode(HTTP_OK)
                .body(matchesJsonSchemaInClasspath("schemas/contact_created.json").using(settings))
                .body("subject", equalTo(random))
                .body("message", equalTo(random))
                .header("Content-Type", containsString(ContentType.JSON.toString()))
                .header("Server", not(emptyString()));

        assertThat(response.time(), lessThan(5000L)); // Temporary 5000 ms
    }

    @Test
    public void createNewContactMissingSubject(){
        String random = generateNewString();
        String requestBody = "{\"first_name\": \"" + random + "\", \"last_name\": \"" + random + "\", \"email\": \"" + random + "\", \"message\": \"" + random +"\"}";

        Response response = given()
                .body(requestBody)
                .contentType(ContentType.JSON)
                .when()
                .post(CONTACT_ENDPOINT);

        response
                .then()
                .assertThat()
                .statusCode(422)
                .body("subject[0]", equalTo("The subject field is required."))
                .header("Content-Type", containsString(ContentType.JSON.toString()))
                .header("Server", not(emptyString()));

        assertThat(response.time(), lessThan(5000L)); // Temporary 5000 ms
    }
    @Test
    public void createNewContactEmptyMessage(){
        String random = generateNewString();
        String requestBody = "{\"first_name\": \"" + random + "\", \"last_name\": \"" + random + "\", \"email\": \"" + random + "\", \"subject\": \"" + random + "\", \"message\": \" \"}";

        Response response = given()
                .body(requestBody)
                .contentType(ContentType.JSON)
                .when()
                .post(CONTACT_ENDPOINT);

        response
                .then()
                .assertThat()
                .statusCode(422)
                .body("message[0]", equalTo("The message field is required."))
                .header("Content-Type", containsString(ContentType.JSON.toString()))
                .header("Server", not(emptyString()));

        assertThat(response.time(), lessThan(5000L)); // Temporary 5000 ms
    }

@Test
public void createNewContactEmptyBody(){

    String requestBody = "{}";

    Response response = given()
            .body(requestBody)
            .contentType(ContentType.JSON)
            .when()
            .post(CONTACT_ENDPOINT);

    response
            .then()
            .assertThat()
            .statusCode(422)
            .body("message[0]", equalTo("The message field is required."))
            .body("subject[0]", equalTo("The subject field is required."))
            .header("Content-Type", containsString(ContentType.JSON.toString()))
            .header("Server", not(emptyString()));

    assertThat(response.time(), lessThan(5000L)); // Temporary 5000 ms
}
}
