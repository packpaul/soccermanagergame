package com.pp.toptal.soccermanager.entity;

import java.util.Date;
import java.util.Objects;

import com.pp.toptal.soccermanager.utils.DateTimeFormatter;

/**
 * Base class for ORM entities.
 * 
 * @param <ID>
 */
public abstract class EntityBase<ID> {
    
    private ID id;
    
    public ID getId() {
        return id;
    }
    public void setId(ID id) {
        this.id = id;
    }
    
    final static <ID> ID getId(EntityBase<ID> entity) {
        return (entity != null) ? entity.getId() : null;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        EntityBase<?> other = (EntityBase<?>) obj;

        return Objects.equals(id, other.id);
    }
    
    @Override
    public int hashCode() {
        return 31 + Objects.hashCode(id);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(getClass().getSimpleName())
            .append('{')
                .append("id=").append(getId())
            .append('}');
        
        return sb.toString();
    }
    
    final static String toString(EntityBase<?> entity) {
        return (entity != null) ? entity.toString() : null;
    }
    
    final static String toDateString(Date date) {
        return DateTimeFormatter.toDateString(date);
    }

    final static String toDateTimeString(Date date) {
        return DateTimeFormatter.toDateTimeString(date);
    }

}
