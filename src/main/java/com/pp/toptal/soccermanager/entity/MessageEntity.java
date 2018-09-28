package com.pp.toptal.soccermanager.entity;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "messages")
public class MessageEntity extends EntityBase<Long> implements Serializable {
    
    private static final long serialVersionUID = 1267839888154208286L;

    public MessageEntity() {
    }
    
    public MessageEntity(UserEntity fromUser, UserEntity toUser, String content) {
        this(fromUser, toUser, content, null);
    }
    
    public MessageEntity(UserEntity fromUser, UserEntity toUser, String content, MessageEntity prevMessage) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.content = content;
        this.prevMessage = prevMessage;
        this.creationDate = new Date();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Override
    public Long getId() {
        return super.getId();
    }
    
    private UserEntity fromUser;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fromuserid", nullable = false)
    public UserEntity getFromUser() {
        return fromUser;
    }
    public void setFromUser(UserEntity fromUser) {
        this.fromUser = fromUser;
    }
    
    private UserEntity toUser;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "touserid", nullable = false)
    public UserEntity getToUser() {
        return toUser;
    }
    public void setToUser(UserEntity toUser) {
        this.toUser = fromUser;
    }

    private MessageEntity prevMessage;
    
    @OneToOne
    @JoinColumn(name = "prevmessageid")
    public MessageEntity getPrevMessage() {
        return prevMessage;
    }
    public void setPrevMessage(MessageEntity prevMessage) {
        this.prevMessage = prevMessage;
    }

    private MessageEntity nextMessage;
    
    @OneToOne
    @JoinColumn(name = "nextmessageid")
    public MessageEntity getNextMessage() {
        return nextMessage;
    }
    public void setNextMessage(MessageEntity nextMessage) {
        this.nextMessage = nextMessage;
    }
    
    private String content;
    
    @Column(name = "content", nullable = false)
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    
    private Boolean read = false;
    
    @Column(name = "read", nullable = false)
    public Boolean isRead() {
        return read;
    }
    public void setRead(Boolean read) {
        this.read = read;
    }
    
    private Date creationDate;

    @Column(name = "creationdate")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    private Date updateDate;

    @Column(name = "updatedate")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getUpdateDate() {
        return updateDate;
    }
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (! super.equals(obj)) {
            return false;
        }
        
        // TODO:
        
        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        // TODO: result = 31 * result + ...;
        
        return result;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(getClass().getSimpleName())
                .append('{')
                    .append("id=").append(getId()).append(',')
                    .append("content='").append(content.substring(0, 25)).append('\'').append(',')
                    .append("read=").append(read).append(',')                    
                    .append("fromUser=").append(getId(fromUser)).append(',')
                    .append("toUser=").append(getId(toUser)).append(',')
                    .append("prevMessage=").append(getId(prevMessage)).append(',')
                    .append("nextMessage=").append(getId(nextMessage)).append(',')
                    .append("creationDate='").append(toDateTimeString(creationDate)).append('\'').append(',')
                    .append("updateDate='").append(toDateTimeString(updateDate)).append('\'')
                .append('}');
            
        return sb.toString();
    }

}
