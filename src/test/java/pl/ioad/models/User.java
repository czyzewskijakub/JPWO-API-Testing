package pl.ioad.models;

public record User(
        String id,
        String provider,
        String first_name,
        String last_name,
        String address,
        String city,
        String state,
        String country,
        String postcode,
        String phone,
        String dob,
        String email,
        int failed_login_attempts,
        String created_at
) {
}

