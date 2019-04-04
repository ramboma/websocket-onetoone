package com.example.websocketonetoone.listener;

import com.example.websocketonetoone.model.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    private  static final Logger logger= LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private SimpMessageSendingOperations simpMessageSendingOperations;

    @EventListener
    public void handlerWebSocketConnectListener(SessionConnectedEvent event)
    {
        StompHeaderAccessor headerAccessor=StompHeaderAccessor.wrap(event.getMessage());
        String sessionid=headerAccessor.getSessionId();
        logger.info("Receive a new connect,session id is"+sessionid);
    }
    @EventListener
    public void handlerWebSocketDisConnectListener(SessionDisconnectEvent event)
    {
        StompHeaderAccessor headerAccessor=StompHeaderAccessor.wrap(event.getMessage());

        String sessionid=headerAccessor.getSessionId();
        if(sessionid!=null)
        {
            logger.info("user disconnect:session id is"+sessionid);
            ChatMessage chatMessage=new ChatMessage();
            chatMessage.setContent(sessionid+" has level");
            chatMessage.setType(ChatMessage.MessageType.LEVEL);

            simpMessageSendingOperations.convertAndSend("/topic/public",chatMessage);
        }
    }
}
