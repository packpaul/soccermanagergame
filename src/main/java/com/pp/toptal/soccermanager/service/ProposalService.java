package com.pp.toptal.soccermanager.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.javatuples.Triplet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pp.toptal.soccermanager.auth.AuthService;
import com.pp.toptal.soccermanager.entity.PlayerEntity;
import com.pp.toptal.soccermanager.entity.TeamEntity;
import com.pp.toptal.soccermanager.entity.TransferEntity;
import com.pp.toptal.soccermanager.entity.UserType;
import com.pp.toptal.soccermanager.entity.TransferEntity.TransferStatus;
import com.pp.toptal.soccermanager.entity.UserEntity;
import com.pp.toptal.soccermanager.entity.ProposalEntity;
import com.pp.toptal.soccermanager.entity.QProposalEntity;
import com.pp.toptal.soccermanager.exception.BusinessException;
import com.pp.toptal.soccermanager.exception.DataParameterException;
import com.pp.toptal.soccermanager.exception.ErrorCode;
import com.pp.toptal.soccermanager.mapper.EntityToSOMapper;
import com.pp.toptal.soccermanager.repo.OffsetBasedPageRequest;
import com.pp.toptal.soccermanager.repo.PlayerRepo;
import com.pp.toptal.soccermanager.repo.ProposalRepo;
import com.pp.toptal.soccermanager.repo.TeamRepo;
import com.pp.toptal.soccermanager.repo.TransferRepo;
import com.pp.toptal.soccermanager.repo.UserRepo;
import com.pp.toptal.soccermanager.so.TableDataSO;
import com.pp.toptal.soccermanager.so.ProposalSO;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;

@Service
public class ProposalService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ProposalService.class);

    @Autowired
    private AuthService authService;
    
    @Autowired
    private TransferRepo transferRepo;

    @Autowired
    private PlayerRepo playerRepo;
    
    @Autowired
    private ProposalRepo proposalRepo;

    @Autowired
    private TeamRepo teamRepo;
    
    @Autowired
    private UserRepo userRepo;
    
    @Autowired
    private EntityToSOMapper toSoMapper;

    public List<ProposalSO> getProposals(SelectionParameters params) {
        return getProposalsInternal(params).getValue0();
    }
    
    public Triplet<List<ProposalSO>, Long, Long> getProposalsInternal(SelectionParameters params) {
        
        Sort sort = null;
        if (params.getOrderProperty() != null) {
            sort = new Sort(Sort.Direction.fromString(params.getOrderDir()), params.getOrderProperty());
        }
        
        final int limit = (params.getLimit() != null) ? params.getLimit().intValue() : Integer.MAX_VALUE; 
        
        OffsetBasedPageRequest pageReq = new OffsetBasedPageRequest(
                (params.getOffset() != null) ? params.getOffset().intValue() : 0,
                limit,
                sort);
        
        Predicate predicate = QProposalEntity.proposalEntity.transfer.status.eq(TransferStatus.OPEN);
        
        if (authService.isCurrentUserType(UserType.TEAM_OWNER)) {
            UserEntity currentUser = userRepo.findOneByUsername(authService.getCurrentUsername());
            predicate = ExpressionUtils.and(predicate,
                    QProposalEntity.proposalEntity.toTeam.owner.eq(currentUser));
        }
        
        // TODO: filtering
/*
        if (params.getFilterProperties() != null) {
            String[] properties = params.getFilterProperties();
            String[] values = params.getFilterValues();
            QProposalEntity entity = QProposalEntity.proposalEntity;
            for (int i = 0; i < properties.length; i++) {
                Predicate propPredicate;
                if (Objects.equals(properties[i], entity.player. firstName.getMetadata().getName())) {
                    String value = values[i];
                    if (! value.matches("[\\w\\* ]+")) {
                        throw new DataParameterException("Filter for 'firstName' should only contain [a-zA-Z_0-9* ] !");
                    }
                    value = value.replace('*', '%');
                    propPredicate = entity.firstName.likeIgnoreCase(value);
                } else if (Objects.equals(properties[i], entity.lastName.getMetadata().getName())) {
                    String value = values[i];
                    if (! value.matches("[\\w\\* ]+")) {
                        throw new DataParameterException("Filter for 'lastName' should only contain [a-zA-Z_0-9* ] !");
                    }
                    value = value.replace('*', '%');
                    propPredicate = entity.lastName.likeIgnoreCase(value);
                } else if (Objects.equals(properties[i], entity.country.getMetadata().getName())) {
                    Country value = Country.valueOf(values[i]);
                    propPredicate = entity.country.eq(value);
                } else if (Objects.equals(properties[i], entity.proposalType.getMetadata().getName())) {
                    ProposalType value = ProposalType.valueOf(values[i]);
                    propPredicate = entity.proposalType.eq(value);
                } else {
                    continue;
                }
                
                predicate = (predicate != null) ? ExpressionUtils.and(predicate, propPredicate) :
                                                  propPredicate;
            }
        }
*/
        final long count = proposalRepo.count(predicate);
        
        List<ProposalSO> proposals = new ArrayList<>(Math.min(limit, (int) count));

        Iterable<ProposalEntity> pi = proposalRepo.findAll(predicate, pageReq);
        for (ProposalEntity p : pi) {
            proposals.add(toSoMapper.map(p, new ProposalSO()));
        }
        
        if (predicate != null) {
            return Triplet.with(proposals, null, count);
        }
        
        return Triplet.with(proposals, count, count);
    }

    public TableDataSO<ProposalSO> listProposals(SelectionParameters params) {
        
        Triplet<List<ProposalSO>, Long, Long> proposalsData = getProposalsInternal(params);
        
        TableDataSO<ProposalSO> result = new TableDataSO<>();
        result.setData(proposalsData.getValue0());
        result.setCountTotal(Optional.ofNullable(
                proposalsData.getValue1()).orElseGet(() -> proposalRepo.count()));
        result.setCountFiltered(proposalsData.getValue2());

        return result;
    }

    public ProposalSO getProposal(Long proposalId) {
        return toSoMapper.map(findProposal(proposalId), new ProposalSO());
    }
    
    private ProposalEntity findProposal(Long proposalId) {
        ProposalEntity proposal = proposalRepo.findOne(proposalId);
        if (proposal == null) {
            throw new BusinessException(ErrorCode.OBJECT_NOT_FOUND,
                    String.format("Proposal (id=%d) not found!", proposalId));
        }
        
        return proposal;
    }
    
    @Transactional
    public ProposalSO createProposal(Long transferId, Long teamId, Long price) {
        
        TransferEntity transfer = transferRepo.findOne(transferId);
        if (transfer == null) {
            throw new BusinessException(ErrorCode.OBJECT_NOT_FOUND,
                    String.format("Transfer (id=%d) not found!", transferId));
        }
        if (transfer.getStatus() != TransferStatus.OPEN) {
            throw new BusinessException(ErrorCode.INVALID_STATE,
                    String.format("Proposal for transfer (id=%d) cannot be created, transfer should be open!", transferId));
        }

        TeamEntity team = (teamId != null) ? teamRepo.findOne(teamId) : null;
        if (team == null) {
            throw new BusinessException(ErrorCode.OBJECT_NOT_FOUND,
                    String.format("Team (id=%d) not found!", teamId));
        }
        if (team.equals(transfer.getFromTeam())) {
            throw new BusinessException(ErrorCode.INVALID_STATE,
                    String.format("Team of proposal (id=%d) cannot be the same as from team of transfer (id=%d)!",
                            team.getId(), transfer.getId()));
        }

        if ((price == null) || (price <= 0)) {
            throw new DataParameterException("Price for proposal should be present and positive!");
        }
        
        LOGGER.info("Creating proposal for transfer (id={}) and team (id={})...", transferId, teamId);

        ProposalEntity proposal = proposalRepo.save(new ProposalEntity(transfer, team, price));
        
        return toSoMapper.map(proposal, new ProposalSO());
    }

    public void removeProposal(Long proposalId) {
        
        ProposalEntity proposal = findProposal(proposalId);

        proposalRepo.delete(proposal.getId());
        
        LOGGER.info("Proposal (id={}) was deleted.", proposal.getId());
    }

    @Transactional
    public void acceptProposal(Long proposalId) {
        
        ProposalEntity proposal = findProposal(proposalId);
        
        LOGGER.info("Accepting proposal (id={})...", proposal.getId());

        TeamEntity toTeam = proposal.getToTeam();
        
        if (toTeam.getBalance() < proposal.getPrice()) {
            throw new BusinessException(ErrorCode.INVALID_STATE,
                    String.format("Balance of team (id=%d) insufficient for accepting proposal (id=%d)!",
                            proposal.getToTeam().getId(), proposal.getId()));
        }
        
        toTeam.setBalance(toTeam.getBalance() - proposal.getPrice());
        toTeam.setUpdateDate(new Date());
        toTeam = teamRepo.save(toTeam);
        
        TransferEntity transfer = proposal.getTransfer();
        transfer.setToTeam(toTeam);
        transfer.setStatus(TransferStatus.CLOSED);
        transfer.setUpdateDate(new Date());
        transfer = transferRepo.save(transfer);
        
        PlayerEntity player = transfer.getPlayer();
        player.setTeam(toTeam);
        player.setInTransfer(false);
        player.setUpdateDate(new Date());
        player = playerRepo.save(player);
        
        proposal.setUpdateDate(new Date());
        proposalRepo.save(proposal);
    }


}
