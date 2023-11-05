package pl.ioad.models;

public record Credentials(String email, String password) {

    public static Credentials ADMIN = new Credentials("admin@practicesoftwaretesting.com", "welcome01");
    public static Credentials CUSTOMER = new Credentials("customer@practicesoftwaretesting.com", "welcome01");

}
