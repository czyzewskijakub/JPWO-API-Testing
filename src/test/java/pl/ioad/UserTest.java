package pl.ioad;

import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class UserTest {

    private final AuthHelper authHelper = new AuthHelper();

    @Test
    public void testGetUsers() {
        var accessToken = authHelper.getAccessToken("admin@practicesoftwaretesting.com", "welcome01");
        var response = given()
                .baseUri("https://api.practicesoftwaretesting.com")
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/users");

        // Check if the response status code is 200
        response.then().statusCode(200);

        // Check if the response body contains the expected number of values
        var totalUsers = response.jsonPath().get("total");
        assertThat(totalUsers, equalTo(2));
    }

}
