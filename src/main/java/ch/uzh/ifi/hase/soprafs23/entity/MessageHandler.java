package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.websocket.dto.MessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.messaging.simp.SimpMessagingTemplate;
        import org.springframework.scheduling.annotation.EnableScheduling;
        import org.springframework.scheduling.annotation.Scheduled;
        import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class MessageHandler {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public MessageHandler(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Scheduled(fixedRate = 2000)
    public void sendMessages() {
        MessageDTO message = new MessageDTO();
        message.setMessage("Hello, world!");
        messagingTemplate.convertAndSend("/topic/messages", message);
    }
}