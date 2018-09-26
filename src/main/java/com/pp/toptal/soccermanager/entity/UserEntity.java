package com.pp.toptal.soccermanager.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "users")
public class UserEntity extends EntityBase<Long> implements Serializable {
    
    private static final long serialVersionUID = 2331956308597772320L;

    public UserEntity() {
    }
    
    public UserEntity(UserType userType) {
        this(null, userType, null);
    }
    
    public UserEntity(String username, UserType userType, String password) {
        this.username = username;
        this.userType = userType;
        this.password = password;
        this.creationDate = new Date();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Override
    public Long getId() {
        return super.getId();
    }
    
    private String username;
    
    @Column(name = "username", nullable = false, unique = true)
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    
    private UserType userType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "usertype", nullable = false)
    public UserType getUserType() {
        return userType;
    }
    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    private transient String password;
    
    @JsonIgnore
    @Column(name = "password")
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
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
    
    private Date lastLoginDate;
    
    @JsonIgnore
    @Column(name = "lastlogindate")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastLoginDate() {
        return lastLoginDate;
    }
    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (! super.equals(obj)) {
            return false;
        }
        
        UserEntity other = (UserEntity) obj;
        
        return Objects.equals(username, other.username);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(username);
        
        return result;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(getClass().getSimpleName())
                .append('{')
                    .append("id=").append(getId()).append(',')
                    .append("username='").append(username).append('\'').append(',')
                    .append("userType=").append(userType).append(',')
                    .append("creationDate='").append(toDateTimeString(creationDate)).append('\'').append(',')
                    .append("updateDate='").append(toDateTimeString(updateDate)).append('\'').append(',')
                    .append("lastLoginDate='").append(toDateTimeString(lastLoginDate)).append('\'')
                .append('}');
            
        return sb.toString();
    }

}
