package com.pp.toptal.soccermanager.repo;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.pp.toptal.soccermanager.entity.Country;
import com.pp.toptal.soccermanager.entity.TeamEntity;

@Repository
public interface TeamRepo extends PagingAndSortingRepository<TeamEntity, Long>,
                                  QueryDslPredicateExecutor<TeamEntity> {

    TeamEntity findOneByTeamNameAndCountry(String teamName, Country country);
    
}
