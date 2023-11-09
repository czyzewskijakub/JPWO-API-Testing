package pl.ioad.utils;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static pl.ioad.utils.UniqueNameGenerator.generateUniqueCategoryName;

public class CategoryCreator {
    public static String createCategory() {
        String categoryName = generateUniqueCategoryName();
        String requestBody = "{\"name\": \"" + categoryName + "\", \"slug\": \"" + categoryName + "_slug\"}";
        Response response = given()
                .body(requestBody)
                .contentType(ContentType.JSON)
                .post("/categories");
        return response.jsonPath().getString("id");
    }
}
