package com.pp.toptal.soccermanager.repo;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.pp.toptal.soccermanager.entity.PlayerEntity;
import com.pp.toptal.soccermanager.entity.TeamEntity;
import com.pp.toptal.soccermanager.entity.TransferEntity;
import com.pp.toptal.soccermanager.entity.TransferEntity.TransferStatus;

@Repository
public interface TransferRepo extends PagingAndSortingRepository<TransferEntity, Long>,
                                      QueryDslPredicateExecutor<TransferEntity> {

    boolean existsByPlayerAndFromTeamAndStatus(
            PlayerEntity player, TeamEntity fromTeam, TransferStatus status);
    
}
