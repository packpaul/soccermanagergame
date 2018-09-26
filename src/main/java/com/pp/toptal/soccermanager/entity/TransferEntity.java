package com.pp.toptal.soccermanager.entity;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "transfers")
public class TransferEntity extends EntityBase<Long> implements Serializable {
    
    private static final long serialVersionUID = -1732199141360380101L;

    public TransferEntity() {
    }
    
    public TransferEntity(PlayerEntity player, TeamEntity fromTeam) {
        this(player, fromTeam, null, null);
    }
    
    public TransferEntity(PlayerEntity player, TeamEntity fromTeam, TeamEntity toTeam, Long price) {
        this.player = player;
        this.fromTeam = fromTeam;
        this.toTeam = toTeam;
        this.price = price;

        this.creationDate = new Date();
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Override
    public Long getId() {
        return super.getId();
    }
    
    private PlayerEntity player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "playerid", nullable = false)
    public PlayerEntity getPlayer() {
        return player;
    }
    public void setPlayer(PlayerEntity player) {
        this.player = player;
    }
    
    private TeamEntity fromTeam;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fromteamid")
    public TeamEntity getFromTeam() {
        return fromTeam;
    }
    public void setFromTeam(TeamEntity fromTeam) {
        this.fromTeam = fromTeam;
    }
    
    private TeamEntity toTeam;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "toteamid")
    public TeamEntity getToTeam() {
        return toTeam;
    }
    public void setToTeam(TeamEntity toTeam) {
        this.toTeam = toTeam;
    }
    
    private Long price;
    
    @Column(name = "price")
    public Long getPrice() {
        return price;
    }
    public void setPrice(Long price) {
        this.price = price;
    }
    
    private TransferStatus status = TransferStatus.OPEN;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    public TransferStatus getStatus() {
        return status;
    }
    public void setStatus(TransferStatus status) {
        this.status = status;
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
                    .append("player=").append(getId(player)).append(',')
                    .append("fromTeam=").append(getId(fromTeam)).append(',')
                    .append("toTeam=").append(getId(toTeam)).append(',')
                    .append("price=").append(price).append(',')
                    .append("status=").append(status).append(',')
                    .append("creationDate='").append(toDateTimeString(creationDate)).append('\'').append(',')
                    .append("updateDate='").append(toDateTimeString(updateDate)).append('\'')
                .append('}');
            
        return sb.toString();
    }
    
    public enum TransferStatus {
        OPEN,
        CLOSED,
        CANCELED
    }

}
