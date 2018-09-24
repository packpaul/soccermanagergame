package com.pp.toptal.soccermanager.entity;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "players")
public class PlayerEntity extends EntityBase<Long> implements Serializable {
    
    private static final long serialVersionUID = -5881557161788838539L;

    public PlayerEntity() {
    }
    
    public PlayerEntity(String firstname, Integer age, Country country) {
        this(firstname, null, age, country);
    }
    
    public PlayerEntity(String firstname, String lastname, Integer age, Country country) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.age = age;
        this.country = country;
        this.creationDate = new Date();
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Override
    public Long getId() {
        return super.getId();
    }
    
    private TeamEntity team;

    @ManyToOne
    @JoinColumn(name = "teamid", nullable = false)
    public TeamEntity getTeam() {
        return team;
    }
    public void setTeam(TeamEntity team) {
        this.team = team;
    }
    
    private String firstname;
    
    @Column(name = "firstname", nullable = false)
    public String getFirstname() {
        return firstname;
    }
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
    
    private String lastname;
    
    @Column(name = "lastname")
    public String getLastname() {
        return lastname;
    }
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    
    private Integer age;
    
    @Column(name = "age", nullable = false)
    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
    
    private PlayerType playerType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "playertype", nullable = false)
    public PlayerType getPlayerType() {
        return playerType;
    }
    public void setPlayerType(PlayerType playerType) {
        this.playerType = playerType;
    }
    
    private Long value; 
    
    @Column(name = "value", nullable = false)
    public Long getValue() {
        return value;
    }
    public void setValue(Long value) {
        this.value = value;
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
    
    private Country country;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "country", nullable = false)
    public Country getCountry() {
        return country;
    }
    public void setCountry(Country country) {
        this.country = country;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (! super.equals(obj)) {
            return false;
        }
        
        PlayerEntity other = (PlayerEntity) obj;
        
        return (Objects.equals(firstname, other.firstname) &&
                Objects.equals(lastname, other.lastname) &&
                Objects.equals(age, other.age) &&
                Objects.equals(country, other.country));
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hash(firstname, lastname, age, country);
        
        return result;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(getClass().getSimpleName())
                .append('{')
                    .append("id=").append(getId()).append(',')
                    .append("firstname='").append(firstname).append('\'').append(',')
                    .append("lastnameType='").append(lastname).append('\'').append(',')
                    .append("age='").append(age).append('\'').append(',')
                    .append("playerType='").append(playerType).append('\'').append(',')
                    .append("country='").append(country).append('\'').append(',')
                    .append("creationDate='").append(toDateTimeString(creationDate)).append('\'').append(',')
                    .append("updateDate='").append(toDateTimeString(updateDate)).append('\'')
                .append('}');
            
        return sb.toString();
    }

    public enum PlayerType {
        GOALKEEPER,
        DEFENDER,
        MIDFIELDER,
        ATTACKER
    }

}
