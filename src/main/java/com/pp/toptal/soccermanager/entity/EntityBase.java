package com.pp.toptal.soccermanager.entity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * Base class for ORM entities.
 * 
 * @param <ID>
 */
public abstract class EntityBase<ID> {
    
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMATTER =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));
    private static final ThreadLocal<SimpleDateFormat> DATE_TIME_FORMATTER =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    
    private ID id;
    
    public ID getID() {
        return id;
    }
    public void setID(ID id) {
        this.id = id;
    }
    
    final static <ID> ID getID(EntityBase<ID> entity) {
        return (entity != null) ? entity.getID() : null;
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
                .append("id=").append(getID())
            .append('}');
        
        return sb.toString();
    }
    
    final static String toString(EntityBase<?> entity) {
        return (entity != null) ? entity.toString() : null;
    }
    
    final static String toDateString(Date date) {
        return (date != null) ? DATE_FORMATTER.get().format(date) : null;
    }

    final static String toDateTimeString(Date date) {
        return (date != null) ? DATE_TIME_FORMATTER.get().format(date) : null;
    }

}
