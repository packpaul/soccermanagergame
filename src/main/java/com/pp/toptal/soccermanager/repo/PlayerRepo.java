package com.pp.toptal.soccermanager.repo;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.pp.toptal.soccermanager.entity.Country;
import com.pp.toptal.soccermanager.entity.PlayerEntity;
import com.pp.toptal.soccermanager.entity.TeamEntity;

@Repository
public interface PlayerRepo extends PagingAndSortingRepository<PlayerEntity, Long>,
                                    QueryDslPredicateExecutor<PlayerEntity> {

    TeamEntity findOneByFirstnameAndLastnameAndCountry(
            String firstname, String lastname, Country country);
    
}
