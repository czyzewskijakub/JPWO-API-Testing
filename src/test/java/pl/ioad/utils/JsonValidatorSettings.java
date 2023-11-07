package pl.ioad.utils;

import io.restassured.module.jsv.JsonSchemaValidatorSettings;

public class JsonValidatorSettings {
    public static final JsonSchemaValidatorSettings settings = JsonSchemaValidatorSettings.settings().with().checkedValidation(false);
}
