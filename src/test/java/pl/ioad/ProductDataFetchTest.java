package pl.ioad;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static pl.ioad.utils.JsonValidatorSettings.settings;
import static java.net.HttpURLConnection.HTTP_OK;


public class ProductDataFetchTest {

    private final static String PRODUCTS_ENDPOINT = "/products";
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://api.practicesoftwaretesting.com";
    }


    @Test
    public void testGetAllProductsPositive() {
        Response response = given()
                .when()
                .get(PRODUCTS_ENDPOINT);

        response
                .then()
                .statusCode(HTTP_OK)
                .assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/products_response.json").using(settings))
                .header("Content-Type", containsString(ContentType.JSON.toString()))
                .header("Server", not(emptyString()));


        assertThat(response.time(), lessThan(5000L)); // Temporary 5000 ms
    }

    @Test
    public void testGetEmptyProductsByBrand() {
        Response response = given()
                .when()
                .queryParam("by_brand", -1)
                .get(PRODUCTS_ENDPOINT);

        response
                .then()
                .statusCode(HTTP_OK)
                .assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/products_response.json").using(settings))
                .body("data", empty())
                .header("Content-Type", containsString(ContentType.JSON.toString()))
                .header("Server", not(emptyString()));


        assertThat(response.time(), lessThan(5000L)); // Temporary 5000 ms
    }

    @Test
    public void testGetProductsByIncorrectCategory() {
        Response response = given()
                .when()
                .queryParam("by_category", -1)
                .get(PRODUCTS_ENDPOINT);

        response
                .then()
                .statusCode(HTTP_OK)
                .assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/products_response.json").using(settings))
                .body("data", empty())
                .header("Content-Type", containsString(ContentType.JSON.toString()))
                .header("Server", not(emptyString()));


        assertThat(response.time(), lessThan(5000L)); // Temporary 5000 ms
    }

    @Test
    public void testBetweenPriceCorrectRange() {
        Response response = given()
                .when()
                .queryParam("between", "price,10,30")
                .get(PRODUCTS_ENDPOINT);

        response
                .then()
                .statusCode(HTTP_OK)
                .assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/products_response.json").using(settings))
                .body("data", not(empty()))
                .header("Content-Type", containsString(ContentType.JSON.toString()))
                .header("Server", not(emptyString()));


        assertThat(response.time(), lessThan(5000L)); // Temporary 5000 ms
    }

    @Test
    public void testBetweenPriceIncorrectRange() {
        Response response = given()
                .when()
                .queryParam("between", "price,30,10")
                .get(PRODUCTS_ENDPOINT);

        response
                .then()
                .statusCode(200)
                .assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/products_response.json").using(settings))
                .body("data", empty())
                .header("Content-Type", containsString(ContentType.JSON.toString()))
                .header("Server", not(emptyString()));


        assertThat(response.time(), lessThan(5000L)); // Temporary 5000 ms
    }

}
