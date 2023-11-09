package pl.ioad.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class UniqueNameGenerator {
    public static String generateUniqueCategoryName() {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String randomString = UUID.randomUUID().toString().replaceAll("-", "");
        return "NewCategory_" + timestamp + "_" + randomString;
    }

    public static String generateNewString(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

}
