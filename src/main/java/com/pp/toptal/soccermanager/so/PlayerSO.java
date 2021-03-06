package com.pp.toptal.soccermanager.so;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class PlayerSO implements Serializable {
    
    private static final long serialVersionUID = 1197380675255313276L;

    private Long id;
    private String firstName;
    private String lastName;
    private String fullName;
    private Integer age;
    private String playerType;
    private Long value;
    private String country;
    
    public static final String TEAM_ID_PROP_NAME = "teamId";
    private Long teamId;

    private String teamName;
    
    public static final String TEAM_COUNTRY_PROP_NAME = "teamCountry";
    private String teamCountry;
    private Long teamOwnerUserId;

    private Boolean inTransfer;
    
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
    
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
    
    public String getPlayerType() {
        return playerType;
    }
    public void setPlayerType(String playerType) {
        this.playerType = playerType;
    }
    
    public Long getValue() {
        return value;
    }
    public void setValue(Long value) {
        this.value = value;
    }
    
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    
    public Long getTeamId() {
        return teamId;
    }
    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
    
    public String getTeamCountry() {
        return teamCountry;
    }
    public void setTeamCountry(String teamCountry) {
        this.teamCountry = teamCountry;
    }
    
    public Long getTeamOwnerUserId() {
        return teamOwnerUserId;
    }
    public void setTeamOwnerUserId(Long teamOwnerUserId) {
        this.teamOwnerUserId = teamOwnerUserId;
    }

    public Boolean isInTransfer() {
        return inTransfer;
    }
    public void setInTransfer(Boolean inTransfer) {
        this.inTransfer = inTransfer;
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
