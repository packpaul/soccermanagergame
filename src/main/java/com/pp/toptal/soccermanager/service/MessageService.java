package com.pp.toptal.soccermanager.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pp.toptal.soccermanager.auth.AuthService;
import com.pp.toptal.soccermanager.entity.MessageEntity;
import com.pp.toptal.soccermanager.entity.UserEntity;
import com.pp.toptal.soccermanager.exception.BusinessException;
import com.pp.toptal.soccermanager.exception.DataParameterException;
import com.pp.toptal.soccermanager.exception.ErrorCode;
import com.pp.toptal.soccermanager.mapper.EntityToSOMapper;
import com.pp.toptal.soccermanager.repo.MessageRepo;
import com.pp.toptal.soccermanager.repo.UserRepo;
import com.pp.toptal.soccermanager.so.MessageSO;

@Service
public class MessageService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageService.class);
    
    @Autowired
    private MessageRepo messageRepo;
    
    @Autowired
    private UserRepo userRepo;
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private EntityToSOMapper toSoMapper;

    public List<MessageSO> getUnreadMessages() {
        
        UserEntity toUser = userRepo.findOneByUsername(authService.getCurrentUsername());
        
        List<MessageEntity> messages = messageRepo.findByToUserAndReadOrderByCreationDate(toUser, false);
        
        return messages.stream()
                .map((m) -> toSoMapper.map(m, new MessageSO()))
                .collect(Collectors.toList());
    }

    @Transactional
    public MessageSO sendMessage(MessageSO message) {
        
        UserEntity toUser = userRepo.findOneByUsername(message.getToUsername());
        if (toUser == null) {
            throw new BusinessException(ErrorCode.OBJECT_NOT_FOUND,
                    String.format("User (username='%s') not found!", message.getToUsername()));
        }
        
        UserEntity fromUser = userRepo.findOneByUsername(authService.getCurrentUsername());
        
        if (StringUtils.isBlank(message.getContent())) {
            throw new DataParameterException("Message content should be present!");
        }
        
        MessageEntity messageEntity = new MessageEntity(fromUser, toUser, message.getContent());
        
        LOGGER.info(String.format("Message (id=%d) is sent from user '%s' to user '%s'",
                message.getId(), fromUser.getUsername(), toUser.getUsername()));
        
        return toSoMapper.map(messageRepo.save(messageEntity), new MessageSO());
    }

    @Transactional
    public MessageSO readMessage(Long messageId) {
        
        MessageEntity message = messageRepo.findOne(messageId);
        if (message == null) {
            throw new BusinessException(ErrorCode.OBJECT_NOT_FOUND,
                    String.format("Message (id=%d) not found!", messageId));
        }
        
        message.setRead(true);
        message.setUpdateDate(new Date());
        
        message = messageRepo.save(message);
        
        return toSoMapper.map(message, new MessageSO());
    }
     
}
