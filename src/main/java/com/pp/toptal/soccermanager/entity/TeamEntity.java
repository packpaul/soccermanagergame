package com.pp.toptal.soccermanager.entity;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Table(name = "teams")
public class TeamEntity extends EntityBase<Long> implements Serializable {
    
    private static final long serialVersionUID = 1957371833148381364L;

    public TeamEntity() {
    }
    
    public TeamEntity(String teamName, Country country) {
        this(teamName, country, null, null);
    }
    
    public TeamEntity(String teamName, Country country, UserEntity owner, Long balance) {
        this.teamName = teamName;
        this.country = country;
        this.owner = owner;
        this.balance = balance;
        
        this.creationDate = new Date();
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Override
    public Long getId() {
        return super.getId();
    }
    
    private UserEntity owner;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userid", nullable = false)
    public UserEntity getOwner() {
        return owner;
    }
    public void setOwner(UserEntity owner) {
        this.owner = owner;
    }
    
    private String teamName;
    
    @Column(name = "teamname", nullable = false)
    public String getTeamName() {
        return teamName;
    }
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
    
    private Country country;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "country", nullable = false)
    public Country getCountry() {
        return country;
    }
    public void setCountry(Country country) {
        this.country = country;
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
    
    private List<PlayerEntity> players;
    
    @OneToMany(mappedBy = "team", cascade = CascadeType.REFRESH)
    public List<PlayerEntity> getPlayers() {
        return (players != null) ? players : Collections.emptyList();
    }
    public void setPlayers(List<PlayerEntity> players) {
        this.players = players;
    }
    
    private Long balance; 
    
    @Column(name = "balance", nullable = false)
    public Long getBalance() {
        return balance;
    }
    public void setBalance(Long balance) {
        this.balance = balance;
    }
    
    @Transient
    public long getValue() {
        return players.stream().collect(Collectors.summingLong((p) -> p.getValue()));
    }
    
    @Override
    public boolean equals(Object obj) {
        if (! super.equals(obj)) {
            return false;
        }
        
        TeamEntity other = (TeamEntity) obj;
        
        return (Objects.equals(teamName, other.teamName) && Objects.equals(country, other.country));
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hash(teamName, country);
        
        return result;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(getClass().getSimpleName())
                .append('{')
                    .append("id=").append(getId()).append(',')
                    .append("teamName='").append(teamName).append('\'').append(',')
                    .append("country=").append(country).append(',')
                    .append("creationDate='").append(toDateTimeString(creationDate)).append('\'').append(',')
                    .append("updateDate='").append(toDateTimeString(updateDate)).append('\'')
                .append('}');
            
        return sb.toString();
    }

}
