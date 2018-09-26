package com.pp.toptal.soccermanager.so;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class MessageSO implements Serializable {
    
    private static final long serialVersionUID = -412756987619720516L;

    private Long id;
    
    private Long fromUserId;
    private String fromUsername;
    
    private Long toUserId;
    private String toUsername;

    private Long nextMessageId;
    private Long prevMessageId;
    
    private String content;
    private Boolean read;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date creationDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date updateDate;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getFromUserId() {
        return fromUserId;
    }
    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getFromUsername() {
        return fromUsername;
    }
    public void setFromUsername(String fromUsername) {
        this.fromUsername = fromUsername;
    }

    public Long getToUserId() {
        return toUserId;
    }
    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    public String getToUsername() {
        return toUsername;
    }
    public void setToUsername(String toUsername) {
        this.toUsername = toUsername;
    }

    public Long getNextMessageId() {
        return nextMessageId;
    }
    public void setNextMessageId(Long nextMessageId) {
        this.nextMessageId = nextMessageId;
    }

    public Long getPrevMessageId() {
        return prevMessageId;
    }
    public void setPrevMessageId(Long prevMessageId) {
        this.prevMessageId = prevMessageId;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getRead() {
        return read;
    }
    public void setRead(Boolean read) {
        this.read = read;
    }

    public Date getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
    
}
