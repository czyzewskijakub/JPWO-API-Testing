package pl.ioad;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.lessThan;
import static pl.ioad.utils.BaseURI.BASE_URI;
import static pl.ioad.utils.CategoryCreator.createCategory;
import static pl.ioad.utils.Credentials.ADMIN;
import static pl.ioad.utils.UniqueNameGenerator.generateUniqueCategoryName;

public class CategoryUpdateTest {

    private String categoryId;
    private static final String CATEGORY_ENDPOINT = "/categories";
    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
        categoryId = createCategory();
    }


    @Test
    public void testUpdateCategoryUnauthorized() {
        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .delete(CATEGORY_ENDPOINT + "/-1");

        response.then()
                .assertThat()
                .statusCode(HTTP_UNAUTHORIZED)
                .body("message", equalTo("Unauthorized"))
                .header("Content-Type", containsString(ContentType.JSON.toString()))
                .header("Server", not(emptyString()));

        assertThat(response.time(), lessThan(5000L)); // Temporary 5000 ms
    }

    @Test
    public void testUpdateCategoryDoesntExist() {
        var accessToken = AuthHelper.getAccessToken(ADMIN);

        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .put(CATEGORY_ENDPOINT + "/-1");

        response.then()
                .assertThat()
                .statusCode(HTTP_OK)
                .body("success", equalTo(false))
                .header("Content-Type", containsString(ContentType.JSON.toString()))
                .header("Server", not(emptyString()));

        assertThat(response.time(), lessThan(5000L)); // Temporary 5000 ms
    }

    @Test
    public void testUpdateCategoryDataChanged() {
        var accessToken = AuthHelper.getAccessToken(ADMIN);

        String categoryName = generateUniqueCategoryName();
        String requestBody = "{\"name\": \"" + categoryName + "\", \"slug\": \"" + categoryName + "_slug\"}";

        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(requestBody)
                .when()
                .put(CATEGORY_ENDPOINT + "/" + categoryId);


        response.then()
                .assertThat()
                .statusCode(HTTP_OK)
                .body("success", equalTo(true))
                .header("Content-Type", containsString(ContentType.JSON.toString()))
                .header("Server", not(emptyString()));

        assertThat(response.time(), lessThan(5000L)); // Temporary 5000 ms
    }

    @Test
    public void testUpdateCategoryChangedToEmpty() {
        var accessToken = AuthHelper.getAccessToken(ADMIN);

        String requestBody = "{\"name\": \"" + "" + "\", \"slug\": \"" + "\"}";

        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(requestBody)
                .when()
                .put(CATEGORY_ENDPOINT + "/" + categoryId);

        response.then()
                .assertThat()
                .statusCode(422)
                .body("name[0]", equalTo("The name field must be a string."))
                .body("slug[0]", equalTo("The slug field must be a string."))
                .header("Content-Type", containsString(ContentType.JSON.toString()))
                .header("Server", not(emptyString()));

        assertThat(response.time(), lessThan(5000L)); // Temporary 5000 ms
    }

    @Test
    public void testUpdateCategoryChangedOnlyOneField() {
        var accessToken = AuthHelper.getAccessToken(ADMIN);
        String categoryName = generateUniqueCategoryName();

        String requestBody = "{\"name\": \"" + categoryName +"\"}";

        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(requestBody)
                .when()
                .put(CATEGORY_ENDPOINT + "/" + categoryId);

        response.then()
                .assertThat()
                .statusCode(HTTP_OK)
                .body("success", equalTo(true))
                .header("Content-Type", containsString(ContentType.JSON.toString()))
                .header("Server", not(emptyString()));

        assertThat(response.time(), lessThan(5000L)); // Temporary 5000 ms
    }
}
