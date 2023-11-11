package pl.ioad;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static pl.ioad.utils.BaseURI.BASE_URI;

public class CartDeleteTest {
    private final static String CART_ENDPOINT = "/carts";
    private String id;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
        Response response = given()
                .post("/carts");
        id =  response.jsonPath().getString("id");
    }

    @Test
    public void DeleteCart(){
        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .delete(CART_ENDPOINT + "/" + id);

        response.then()
                .assertThat()
                .statusCode(204)
                .header("Server", not(emptyString()));

        assertThat(response.time(), lessThan(5000L)); // Temporary 5000 ms
    }

    @Test
    public void DeleteNotExistingCart(){
        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .delete(CART_ENDPOINT + "/not_existing");

        response.then()
                .assertThat()
                .statusCode(201)
                .body("message",equalTo("Cart doesnt exists"))
                .header("Content-Type", containsString(ContentType.JSON.toString()))
                .header("Server", not(emptyString()));

        assertThat(response.time(), lessThan(5000L)); // Temporary 5000 ms
    }

    @Test
    public void DeleteCartWithoutId(){
        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .delete(CART_ENDPOINT);

        response.then()
                .assertThat()
                .statusCode(405)
                .body("message",equalTo("Method is not allowed for the requested route"))
                .header("Content-Type", containsString(ContentType.JSON.toString()))
                .header("Server", not(emptyString()));

        assertThat(response.time(), lessThan(5000L)); // Temporary 5000 ms
    }
    @Test
    public void testDeleteCartWithRequestBody() {
        Response response = given()
                .body("{id: " + id + "}")
                .contentType(ContentType.JSON)
                .when()
                .delete(CART_ENDPOINT + "/" + id);

        response.then()
                .assertThat()
                .statusCode(HTTP_NO_CONTENT)
                .header("Server", not(emptyString()));

        assertThat(response.time(), lessThan(5000L)); // Temporary 5000 ms
    }

    @Test
    public void testDeleteCartWithQueryParams() {
        Response response = given()
                .queryParam("id", id)
                .contentType(ContentType.JSON)
                .when()
                .delete(CART_ENDPOINT + "/" + id);

        response.then()
                .assertThat()
                .statusCode(HTTP_NO_CONTENT)
                .header("Server", not(emptyString()));

        assertThat(response.time(), lessThan(5000L)); // Temporary 5000 ms
    }

}
