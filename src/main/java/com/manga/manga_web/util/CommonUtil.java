package com.manga.manga_web.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class CommonUtil {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String concatUserNameFromEmail(String email) {
        return email.split("@")[0];
    }

    public static List<String> convertStringArrayToList(String str) {
        return Arrays.asList(str.split(","));
    }

    public static String getCurrentDateTime() {
        return LocalDateTime.now().format(DATE_TIME_FORMATTER);
    }
}
