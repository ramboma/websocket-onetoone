package com.example.websocketonetoone.controller;

import com.example.websocketonetoone.model.ChatMessage;
import com.example.websocketonetoone.model.UserInfo;
import com.example.websocketonetoone.repository.UserRepository;
import org.hibernate.criterion.Example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    private static final Logger logger= LoggerFactory.getLogger(ChatController.class);
    @Autowired
    private SimpMessageSendingOperations simpMessageSendingOperations;
    @Autowired
    private UserRepository userRepository;
    @MessageMapping("/chat.sendMessage")
    public ChatMessage sendMessage(@Payload ChatMessage message)
    {
        Integer receiverId=message.getToId();
        UserInfo receiveUser=userRepository.findById(receiverId).get();


        simpMessageSendingOperations.convertAndSendToUser(receiveUser.getSessionId(),"/queue/getmsg",message,createHeaders(receiveUser.getSessionId()));
        return message;
    }
    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage message, SimpMessageHeaderAccessor headerAccessor)
    {
        UserInfo userInfo=new UserInfo();
        userInfo.setName(message.getSender());
        userInfo.setSessionId(headerAccessor.getSessionId());
        userRepository.save(userInfo);
        message.setFromId(userInfo.getId());
        logger.info(userInfo.getName());
        logger.info(userInfo.getSessionId());
        logger.info(userInfo.getId().toString());
        return message;
    }
}
