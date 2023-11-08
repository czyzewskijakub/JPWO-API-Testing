package pl.ioad;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import pl.ioad.utils.Credentials;

public class AuthHelper {

    public static String getAccessToken(Credentials credentials) {

        HttpClient httpClient = HttpClients.createDefault();

        try {
            HttpPost httpPost = new HttpPost("https://api.practicesoftwaretesting.com/users/login");

            String requestBody = "{\"email\":\"" + credentials.email() + "\",\"password\":\"" + credentials.password() + "\"}";

            httpPost.setEntity(new StringEntity(requestBody));
            httpPost.setHeader("Content-Type", "application/json");

            HttpResponse response = httpClient.execute(httpPost);

            if (response.getStatusLine().getStatusCode() == 200) {

                String responseJson = EntityUtils.toString(response.getEntity());
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(responseJson);
                return jsonNode.get("access_token").asText();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
