package pl.ioad;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static pl.ioad.utils.JsonValidatorSettings.settings;
import static pl.ioad.utils.UniqueNameGenerator.generateUniqueCategoryName;

public class CategoryCreateTest {

    private static final String CATEGORIES_ENDPOINT = "/categories";
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://api.practicesoftwaretesting.com";
    }

    @Test
    public void testAddNewCategory() {
        String categoryName = generateUniqueCategoryName();
        String requestBody = "{\"name\": \"" + categoryName + "\", \"slug\": \"" + categoryName + "_slug\"}";

        Response response = given()
                .body(requestBody)
                .contentType(ContentType.JSON)
                .when()
                .post(CATEGORIES_ENDPOINT);

        response
                .then()
                .assertThat()
                .statusCode(HTTP_CREATED)
                .body(matchesJsonSchemaInClasspath("schemas/category_created.json").using(settings))
                .body("name", equalTo(categoryName))
                .body("slug", equalTo(categoryName + "_slug"))
                .header("Content-Type", containsString(ContentType.JSON.toString()))
                .header("Server", not(emptyString()));

        assertThat(response.time(), lessThan(5000L)); // Temporary 5000 ms
    }
    @Test
    public void testAddNewCategoryMissingName() {
        String categoryName = generateUniqueCategoryName();
        String requestBody = "{\"slug\": \"" + categoryName + "_slug\"}";

        Response response = given()
                .body(requestBody)
                .contentType(ContentType.JSON)
                .when()
                .post("/categories");

        response
                .then()
                .assertThat()
                .statusCode(422)
                .body("name[0]", equalTo("The name field is required."));

        assertThat(response.time(), lessThan(5000L)); // Temporary 5000 ms
    }
    @Test
    public void testAddNewCategoryMissingSlug() {
        String categoryName = generateUniqueCategoryName();
        String requestBody = "{\"name\": \"" + categoryName + "\"}";

        Response response = given()
                .body(requestBody)
                .contentType(ContentType.JSON)
                .when()
                .post(CATEGORIES_ENDPOINT);

        response
                .then()
                .assertThat()
                .statusCode(422)
                .body("slug[0]", equalTo("The slug field is required."))
                .header("Content-Type", containsString(ContentType.JSON.toString()))
                .header("Server", not(emptyString()));

        assertThat(response.time(), lessThan(5000L)); // Temporary 5000 ms
    }
    @Test
    public void testDuplicateCategory() {
        String categoryName = generateUniqueCategoryName();
        String requestBody = "{\"name\": \"" + categoryName + "\", \"slug\": \"" + categoryName + "_slug\"}";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post(CATEGORIES_ENDPOINT)
                .then()
                .assertThat()
                .statusCode(HTTP_CREATED)
                .contentType(ContentType.JSON)
                .body("name", equalTo(categoryName));

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post("/categories");

        response.then()
                .statusCode(422)
                .body("slug[0]", equalTo("A category already exists with this slug."))
                .header("Content-Type", containsString(ContentType.JSON.toString()))
                .header("Server", not(emptyString()));

        assertThat(response.time(), lessThan(5000L)); // Temporary 5000 ms
    }
    @Test
    public void testTooManyFields() {
        String categoryName = generateUniqueCategoryName();
        String requestBody = "{\"name\": \"" + categoryName + "\", \"slug\": \"" + categoryName + "_slug\", \"notNeededField\": \"value for this field is not needed as well\"}";

        Response response = given()
                .body(requestBody)
                .contentType(ContentType.JSON)
                .when()
                .post(CATEGORIES_ENDPOINT);

        response
                .then()
                .assertThat()
                .statusCode(HTTP_CREATED)
                .body(matchesJsonSchemaInClasspath("schemas/category_created.json").using(settings))
                .body("name", equalTo(categoryName))
                .body("slug", equalTo(categoryName + "_slug"))
                .header("Content-Type", containsString(ContentType.JSON.toString()))
                .header("Server", not(emptyString()));

        assertThat(response.time(), lessThan(5000L)); // Temporary 5000 ms
    }

    @Test
    public void testEmptyBody() {
        String requestBody = "{}";

        Response response = given()
                .body(requestBody)
                .contentType(ContentType.JSON)
                .when()
                .post(CATEGORIES_ENDPOINT);

        response
                .then()
                .assertThat()
                .statusCode(422)
                .body("name[0]", equalTo("The name field is required."))
                .body("slug[0]", equalTo("The slug field is required."))
                .header("Content-Type", containsString(ContentType.JSON.toString()))
                .header("Server", not(emptyString()));

        assertThat(response.time(), lessThan(5000L)); // Temporary 5000 ms
    }
}
