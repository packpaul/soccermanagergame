package com.pp.toptal.soccermanager.repo;

import java.util.List;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.pp.toptal.soccermanager.entity.MessageEntity;
import com.pp.toptal.soccermanager.entity.UserEntity;

@Repository
public interface MessageRepo extends PagingAndSortingRepository<MessageEntity, Long>,
                                      QueryDslPredicateExecutor<MessageEntity> {
    
    public List<MessageEntity> findByToUserAndReadOrderByCreationDate(UserEntity toUser, Boolean isRead);
    
}
