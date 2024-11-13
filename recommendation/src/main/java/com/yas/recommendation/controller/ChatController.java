package com.yas.recommendation.controller;

import com.yas.recommendation.chat.ChatService;
import java.io.IOException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("cs")
@CrossOrigin("*")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/chat")
    public String chat(@RequestParam("message") String message) {
        return chatService.chat(message);
    }

}
