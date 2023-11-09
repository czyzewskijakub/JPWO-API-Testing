package pl.ioad;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static pl.ioad.utils.Credentials.ADMIN;
import static pl.ioad.utils.Credentials.CUSTOMER;
import static pl.ioad.utils.JsonValidatorSettings.settings;

public class ContactDataFetchTest {
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://api.practicesoftwaretesting.com";
    }
    private static final String CONTACT_ENDPOINT = "/messages";

    @Test
    public void getMessagesAsUser(){
        var accessToken = AuthHelper.getAccessToken(CUSTOMER);

        Response response = given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get(CONTACT_ENDPOINT);
        response .then()
                .statusCode(HTTP_OK)
                .assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/contact_response.json").using(settings))
                .header("Content-Type", containsString(ContentType.JSON.toString()))
                .header("Server", not(emptyString()));

    }

    @Test
    public void getMessagesAsAdmin(){
        var accessToken = AuthHelper.getAccessToken(ADMIN);
        Response response = given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get(CONTACT_ENDPOINT);
        response .then()
                .statusCode(HTTP_OK)
                .assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/contact_response.json").using(settings))
                .header("Content-Type", containsString(ContentType.JSON.toString()))
                .header("Server", not(emptyString()));
    }

    @Test
    public void getMessagesWithoutAuthentication(){
        Response response = given()
                .when()
                .get(CONTACT_ENDPOINT);

        response.then()
                .statusCode(HTTP_UNAUTHORIZED)
                .assertThat()
                .body("message", equalTo("Unauthorized"))
                .header("Content-Type", containsString(ContentType.JSON.toString()))
                .header("Server", not(emptyString()));
    }

    @Test
    public void getEmptyMessageById(){
        var accessToken = AuthHelper.getAccessToken(CUSTOMER);
        Response response = given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get(CONTACT_ENDPOINT+"/test");

        response
                .then()
                .statusCode(HTTP_OK)
                .assertThat()
                .body("data", empty())
                .header("Content-Type", containsString(ContentType.JSON.toString()))
                .header("Server", not(emptyString()));


        assertThat(response.time(), lessThan(5000L)); // Temporary 5000 ms
    }

    }


