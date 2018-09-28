package com.pp.toptal.soccermanager.repo;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.pp.toptal.soccermanager.entity.UserEntity;

@Repository
public interface UserRepo extends PagingAndSortingRepository<UserEntity, Long>,
                                  QueryDslPredicateExecutor<UserEntity> {

    UserEntity findOneByUsername(String username);
    
}
