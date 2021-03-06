package com.pp.toptal.soccermanager.so;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ProposalSO implements Serializable {
    
    private static final long serialVersionUID = -674098704046761992L;

    private Long id;
    
    private Long transferId;
    
    public static final String PLAYER_ID_PROP_NAME = "playerId";
    private Long playerId;
    
    private String playerFullName;
    private String playerType;
    private Integer playerAge;
    private String playerCountry;
    private Long playerValue;

    private Long transferFromTeamOwnerUserId;
    
    public static final String TO_TEAM_ID_PROP_NAME = "toTeamId";
    private Long toTeamId;
    
    private String toTeamName;
    private String toTeamCountry;
    private Long toTeamOwnerUserId;
    private String toTeamOwnerUsername;
    
    private Long price;
    
    public static final String CREATION_DATE_PROP_NAME = "creationDate";
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

    public Long getTransferId() {
        return transferId;
    }
    public void setTransferId(Long transferId) {
        this.transferId = transferId;
    }

    public Long getPlayerId() {
        return playerId;
    }
    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public String getPlayerFullName() {
        return playerFullName;
    }
    public void setPlayerFullName(String playerFullName) {
        this.playerFullName = playerFullName;
    }

    public String getPlayerType() {
        return playerType;
    }
    public void setPlayerType(String playerType) {
        this.playerType = playerType;
    }
    
    public Integer getPlayerAge() {
        return playerAge;
    }
    public void setPlayerAge(Integer playerAge) {
        this.playerAge = playerAge;
    }

    public String getPlayerCountry() {
        return playerCountry;
    }
    public void setPlayerCountry(String playerCountry) {
        this.playerCountry = playerCountry;
    }

    public Long getPlayerValue() {
        return playerValue;
    }
    public void setPlayerValue(Long playerValue) {
        this.playerValue = playerValue;
    }
    
    public Long getTransferFromTeamOwnerUserId() {
        return transferFromTeamOwnerUserId;
    }
    public void setTransferFromTeamOwnerUserId(Long transferFromTeamOwnerUserId) {
        this.transferFromTeamOwnerUserId = transferFromTeamOwnerUserId;
    }

    public Long getToTeamId() {
        return toTeamId;
    }
    public void setToTeamId(Long toTeamId) {
        this.toTeamId = toTeamId;
    }

    public String getToTeamName() {
        return toTeamName;
    }
    public void setToTeamName(String toTeamName) {
        this.toTeamName = toTeamName;
    }

    public String getToTeamCountry() {
        return toTeamCountry;
    }
    public void setToTeamCountry(String toTeamCountry) {
        this.toTeamCountry = toTeamCountry;
    }

    public Long getToTeamOwnerUserId() {
        return toTeamOwnerUserId;
    }
    public void setToTeamOwnerUserId(Long toTeamOwnerUserId) {
        this.toTeamOwnerUserId = toTeamOwnerUserId;
    }

    public String getToTeamOwnerUsername() {
        return toTeamOwnerUsername;
    }
    public void setToTeamOwnerUsername(String toTeamOwnerUsername) {
        this.toTeamOwnerUsername = toTeamOwnerUsername;
    }

    public Long getPrice() {
        return price;
    }
    public void setPrice(Long price) {
        this.price = price;
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
