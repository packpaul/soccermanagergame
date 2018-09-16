package com.pp.toptal.soccermanager.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.pp.toptal.soccermanager.entity.UserEntity;

import java.util.List;

@Repository
public interface UserRepo extends CrudRepository<UserEntity, Long> {

    List<UserEntity> findByUsername(String username);
    
    UserEntity findOneByUsername(String username);
}
