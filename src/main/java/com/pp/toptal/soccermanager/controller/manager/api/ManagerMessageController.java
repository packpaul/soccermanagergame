package com.pp.toptal.soccermanager.controller.manager.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pp.toptal.soccermanager.service.MessageService;
import com.pp.toptal.soccermanager.so.MessageSO;

@RestController()
@RequestMapping(ManagerApi.ROOT + "/v" + ManagerApi.BASELINE_VERSION + "/message")
public class ManagerMessageController {

    @Autowired
    private MessageService messageService;

    /**
     * Retreives unread messages of current user 
     */
    @GetMapping
    public List<MessageSO> getUnreadMessages() {
        return messageService.getUnreadMessages();
    }

    @PostMapping
    public MessageSO sendMessage(@RequestBody MessageSO message) {
        return messageService.sendMessage(message);
    }
    
    @PostMapping("/{messageId}/read")
    public MessageSO readMessage(@PathVariable("messageId") Long messageId) {
        return messageService.readMessage(messageId);
    }

}