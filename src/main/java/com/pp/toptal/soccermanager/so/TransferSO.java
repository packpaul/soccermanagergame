package com.pp.toptal.soccermanager.so;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class TransferSO implements Serializable {
    
    private static final long serialVersionUID = -674098704046761992L;

    private Long id;

    private Long playerId;
    private String playerFullName;
    private String playerType;
    private String playerCountry;
    
    private Long fromTeamId;
    private String fromTeamName;
    private String fromTeamCountry;

    private Long toTeamId;
    private String toTeamName;
    private String toTeamCountry;
    
    private Long price;
    private String status;
    
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

    public String getPlayerCountry() {
        return playerCountry;
    }
    public void setPlayerCountry(String playerCountry) {
        this.playerCountry = playerCountry;
    }

    public Long getFromTeamId() {
        return fromTeamId;
    }
    public void setFromTeamId(Long fromTeamId) {
        this.fromTeamId = fromTeamId;
    }

    public String getFromTeamName() {
        return fromTeamName;
    }
    public void setFromTeamName(String fromTeamName) {
        this.fromTeamName = fromTeamName;
    }

    public String getFromTeamCountry() {
        return fromTeamCountry;
    }
    public void setFromTeamCountry(String fromTeamCountry) {
        this.fromTeamCountry = fromTeamCountry;
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

    public Long getPrice() {
        return price;
    }
    public void setPrice(Long price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
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
