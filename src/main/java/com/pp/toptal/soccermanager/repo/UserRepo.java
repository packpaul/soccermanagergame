package com.pp.toptal.soccermanager.repo;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.pp.toptal.soccermanager.entity.UserEntity;

import java.util.List;

@Repository
public interface UserRepo extends PagingAndSortingRepository<UserEntity, Long>,
                                  QueryDslPredicateExecutor<UserEntity> {

    List<UserEntity> findByUsername(String username);

    UserEntity findOneByUsername(String username);
    
}
