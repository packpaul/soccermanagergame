package com.pp.toptal.soccermanager.so;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class TeamSO implements Serializable {
    
    private static final long serialVersionUID = -7041204562918397396L;

    private Long id;
    private String teamName;
    private String country;
    
    public static final String OWNER_ID_PROP_NAME = "ownerId";
    private Long ownerId;
    
    private String ownerUsername;
    private Long value;
    private Long balance;
    
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
    
    public String getTeamName() {
        return teamName;
    }
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    
    public Long getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
    
    public String getOwnerUsername() {
        return ownerUsername;
    }
    public void setOwnerUsename(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public Long getValue() {
        return value;
    }
    public void setValue(Long value) {
        this.value = value;
    }

    public Long getBalance() {
        return balance;
    }
    public void setBalance(Long balance) {
        this.balance = balance;
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
