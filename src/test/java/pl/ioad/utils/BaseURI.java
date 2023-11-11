package pl.ioad.utils;

import io.github.cdimascio.dotenv.Dotenv;

public class BaseURI {
    public static final String BASE_URI = getBaseUri();

    private static String getBaseUri() {
        Dotenv dotenv = Dotenv.configure().load();
        return dotenv.get("BASE_URI");
    }

}
