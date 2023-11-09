package pl.ioad;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static pl.ioad.utils.CategoryCreator.createCategory;
import static pl.ioad.utils.Credentials.ADMIN;

public class CategoryDeleteTest {

    private final static String CATEGORY_ENDPOINT = "/categories";
    private final List<String> categoriesIds = new ArrayList<>();

    private final int categoriesSize = getClass().getDeclaredMethods().length - 1;
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://api.practicesoftwaretesting.com";
        IntStream.range(0, categoriesSize).forEach(i -> categoriesIds.add(createCategory()));
    }


    @Test
    public void testDeleteCategoryUnauthorized() {
        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .delete(CATEGORY_ENDPOINT + "/" + categoriesIds.get(0));

        response.then()
                .assertThat()
                .statusCode(HTTP_UNAUTHORIZED)
                .header("Content-Type", containsString(ContentType.JSON.toString()))
                .header("Server", not(emptyString()));

        assertThat(response.time(), lessThan(5000L)); // Temporary 5000 ms
    }

    @Test
    public void testDeleteCategoryAuthorized() {
        var accessToken = AuthHelper.getAccessToken(ADMIN);
        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete(CATEGORY_ENDPOINT + "/" + categoriesIds.get(0));

        response.then()
                .assertThat()
                .statusCode(HTTP_NO_CONTENT)
                .header("Server", not(emptyString()));

        assertThat(response.time(), lessThan(5000L)); // Temporary 5000 ms
        categoriesIds.remove(0);
    }

    @Test
    public void testDeleteCategoryIncorrectCategoryId() {
        var accessToken = AuthHelper.getAccessToken(ADMIN);
        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete(CATEGORY_ENDPOINT + "/-1");

        response.then()
                .assertThat()
                .statusCode(422)
                .body("id[0]", equalTo("The selected id is invalid."))
                .header("Content-Type", containsString(ContentType.JSON.toString()))
                .header("Server", not(emptyString()));

        assertThat(response.time(), lessThan(5000L)); // Temporary 5000 ms
    }

    @Test
    public void testDeleteCategoryWithQueryParams() {
        var accessToken = AuthHelper.getAccessToken(ADMIN);
        Response response = given()
                .queryParam("id", categoriesIds.get(0))
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete(CATEGORY_ENDPOINT + "/" + categoriesIds.get(0));

        response.then()
                .assertThat()
                .statusCode(HTTP_NO_CONTENT)
                .header("Server", not(emptyString()));

        assertThat(response.time(), lessThan(5000L)); // Temporary 5000 ms
        categoriesIds.remove(0);
    }

    @Test
    public void testDeleteCategoryWithRequestBody() {
        var accessToken = AuthHelper.getAccessToken(ADMIN);
        Response response = given()
                .body("{id: " + categoriesIds.get(0) + "}")
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete(CATEGORY_ENDPOINT + "/" + categoriesIds.get(0));

        response.then()
                .assertThat()
                .statusCode(HTTP_NO_CONTENT)
                .header("Server", not(emptyString()));

        assertThat(response.time(), lessThan(5000L)); // Temporary 5000 ms
        categoriesIds.remove(0);
    }

}
