package com.manga.manga_web.util;

import java.util.UUID;

public class UserUtil {

    public static String generateUserToken(String userName) {
        return userName + "-" + UUID.randomUUID().toString().replace("-", "");
    }
}
