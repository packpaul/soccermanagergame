package com.pp.toptal.soccermanager.repo;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.pp.toptal.soccermanager.entity.ProposalEntity;

@Repository
public interface ProposalRepo extends PagingAndSortingRepository<ProposalEntity, Long>,
                                      QueryDslPredicateExecutor<ProposalEntity> {

}
