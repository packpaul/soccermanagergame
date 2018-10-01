package com.pp.toptal.soccermanager.entity;

import javax.persistence.*;

import com.querydsl.core.annotations.QueryInit;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "proposals")
public class ProposalEntity extends EntityBase<Long> implements Serializable {
    
    private static final long serialVersionUID = 5063436800051245922L;

    public ProposalEntity() {
    }
    
    public ProposalEntity(TransferEntity transfer, TeamEntity toTeam, Long price) {
        this.transfer = transfer;
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

    @QueryInit("fromTeam.owner")
    private TransferEntity transfer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "transferid", nullable = false)
    
    public TransferEntity getTransfer() {
        return transfer;
    }
    public void setTransfer(TransferEntity transfer) {
        this.transfer = transfer;
    }

    @QueryInit("owner")
    private TeamEntity toTeam;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "toteamid", nullable = false)
    public TeamEntity getToTeam() {
        return toTeam;
    }
    public void setToTeam(TeamEntity toTeam) {
        this.toTeam = toTeam;
    }
    
    private Long price;
    
    @Column(name = "price", nullable = false)
    public Long getPrice() {
        return price;
    }
    public void setPrice(Long price) {
        this.price = price;
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
                    .append("transfer=").append(getId(transfer)).append(',')
                    .append("toTeam=").append(getId(toTeam)).append(',')
                    .append("price=").append(price).append(',')
                    .append("creationDate='").append(toDateTimeString(creationDate)).append('\'').append(',')
                    .append("updateDate='").append(toDateTimeString(updateDate)).append('\'')
                .append('}');
            
        return sb.toString();
    }

}
