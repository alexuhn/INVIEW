package com.ssafy.api.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.ssafy.db.entity.ChatMessage;

@Controller
public class ChatMessageController {

    private final SimpMessagingTemplate template;

    @Autowired
    public ChatMessageController(SimpMessagingTemplate template) {
        this.template = template;
    }

    @MessageMapping("/chat/join")
    public void join(@Payload ChatMessage message) {
        message.setMessage(message.getUserName() + "님이 입장하셨습니다.");
        this.template.convertAndSend("/subscribe/chat/room/" + message.getMeetingId(), message);
    }

    @MessageMapping("/chat/message")
    public void message(@Payload ChatMessage message) {
    	this.template.convertAndSend("/subscribe/chat/room/" + message.getMeetingId(), message);
    }
}
