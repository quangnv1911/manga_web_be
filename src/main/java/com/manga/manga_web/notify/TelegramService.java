package com.manga.manga_web.notify;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TelegramService {

    public void sendMessage(String message) {
        // Implement Telegram message sending logic here
        System.out.println("Sending Telegram message: " + message);
    }
}
