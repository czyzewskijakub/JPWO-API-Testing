package pl.ioad;

import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class BrandsTest {

    @Test
    public void testGetBrands() {
        given()
                .baseUri("https://api.practicesoftwaretesting.com") // Replace with your API base URL
                .when()
                .get("/brands") // Replace with your API endpoint
                .then()
                .statusCode(200);
//                .body("id", equalTo(1))
//                .body("name", equalTo("John Doe"));
    }

}
