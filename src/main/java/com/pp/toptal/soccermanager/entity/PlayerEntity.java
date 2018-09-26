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
    
    public PlayerEntity(String firstName, Integer age, Country country) {
        this(firstName, null, age, country);
    }
    
    public PlayerEntity(String firstName, String lastName, Integer age, Country country) {
        this.firstName = firstName;
        this.lastName = lastName;
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "teamid", nullable = false)
    public TeamEntity getTeam() {
        return team;
    }
    public void setTeam(TeamEntity team) {
        this.team = team;
    }
    
    private String firstName;
    
    @Column(name = "firstname", nullable = false)
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    private String lastName;
    
    @Column(name = "lastname")
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
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
    
    private Country country;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "country", nullable = false)
    public Country getCountry() {
        return country;
    }
    public void setCountry(Country country) {
        this.country = country;
    }
    
    private Boolean inTransfer;

    @Column(name = "intransfer", nullable = false)
    public Boolean isInTransfer() {
        return inTransfer;
    }
    public void setInTransfer(Boolean inTransfer) {
        this.inTransfer = inTransfer;
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
    
    @Transient
    public String getFullName() {

        String fullName = getFirstName();
        if (getLastName() != null) {
            fullName += " " + getLastName();
        }
        
        return fullName;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (! super.equals(obj)) {
            return false;
        }
        
        PlayerEntity other = (PlayerEntity) obj;
        
        return (Objects.equals(firstName, other.firstName) &&
                Objects.equals(lastName, other.lastName) &&
                Objects.equals(age, other.age) &&
                Objects.equals(country, other.country));
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hash(firstName, lastName, age, country);
        
        return result;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(getClass().getSimpleName())
                .append('{')
                    .append("id=").append(getId()).append(',')
                    .append("firstName='").append(firstName).append('\'').append(',')
                    .append("lastName='").append(lastName).append('\'').append(',')
                    .append("age=").append(age).append(',')
                    .append("playerType=").append(playerType).append(',')
                    .append("country=").append(country).append(',')
                    .append("inTransfer=").append(inTransfer).append(',')
                    .append("creationDate='").append(toDateTimeString(creationDate)).append('\'').append(',')
                    .append("updateDate='").append(toDateTimeString(updateDate)).append('\'')
                .append('}');
            
        return sb.toString();
    }

}
