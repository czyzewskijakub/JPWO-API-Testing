package pl.ioad.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public record DBUser(
        String id,
        @JsonProperty("first_name")
        String firstName,
        @JsonProperty("last_name")
        String lastName,
        String address,
        String city,
        String state,
        String country,
        String postcode,
        String phone,
        String dob,
        String email,
        String password,
        @JsonIgnore
        @JsonProperty("created_at")
        String createdAt
) {
}
