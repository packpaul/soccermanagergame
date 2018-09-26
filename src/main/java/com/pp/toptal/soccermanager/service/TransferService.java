package com.pp.toptal.soccermanager.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.javatuples.Triplet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pp.toptal.soccermanager.entity.PlayerEntity;
import com.pp.toptal.soccermanager.entity.TransferEntity;
import com.pp.toptal.soccermanager.entity.TransferEntity.TransferStatus;
import com.pp.toptal.soccermanager.exception.BusinessException;
import com.pp.toptal.soccermanager.exception.ErrorCode;
import com.pp.toptal.soccermanager.mapper.EntityToSOMapper;
import com.pp.toptal.soccermanager.repo.OffsetBasedPageRequest;
import com.pp.toptal.soccermanager.repo.PlayerRepo;
import com.pp.toptal.soccermanager.repo.TransferRepo;
import com.pp.toptal.soccermanager.so.TableDataSO;
import com.pp.toptal.soccermanager.so.TransferSO;
import com.querydsl.core.types.Predicate;

@Service
public class TransferService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TransferService.class);

    @Autowired
    private PlayerRepo playerRepo;
    
    @Autowired
    private TransferRepo transferRepo;
    
    @Autowired
    private EntityToSOMapper toSoMapper;
    
    public List<TransferSO> getTransfers(SelectionParameters params) {
        return getTransfersInternal(params).getValue0();
    }
    
    public Triplet<List<TransferSO>, Long, Long> getTransfersInternal(SelectionParameters params) {
        
        Sort sort = null;
        if (params.getOrderProperty() != null) {
            sort = new Sort(Sort.Direction.fromString(params.getOrderDir()), params.getOrderProperty());
        }
        
        final int limit = (params.getLimit() != null) ? params.getLimit().intValue() : Integer.MAX_VALUE; 
        
        OffsetBasedPageRequest pageReq = new OffsetBasedPageRequest(
                (params.getOffset() != null) ? params.getOffset().intValue() : 0,
                limit,
                sort);
        
        Predicate predicate = null;
        
        // TODO: filtering
/*
        if (params.getFilterProperties() != null) {
            String[] properties = params.getFilterProperties();
            String[] values = params.getFilterValues();
            QTransferEntity entity = QTransferEntity.transferEntity;
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
                } else if (Objects.equals(properties[i], entity.transferType.getMetadata().getName())) {
                    TransferType value = TransferType.valueOf(values[i]);
                    propPredicate = entity.transferType.eq(value);
                } else {
                    continue;
                }
                
                predicate = (predicate != null) ? ExpressionUtils.and(predicate, propPredicate) :
                                                  propPredicate;
            }
        }
*/
        final long count = transferRepo.count(predicate);
        
        List<TransferSO> transfers = new ArrayList<>(Math.min(limit, (int) count));

        Iterable<TransferEntity> pi = transferRepo.findAll(predicate, pageReq);
        for (TransferEntity p : pi) {
            transfers.add(toSoMapper.map(p, new TransferSO()));
        }
        
        if (predicate != null) {
            return Triplet.with(transfers, null, count);
        }
        
        return Triplet.with(transfers, count, count);
    }

    public TableDataSO<TransferSO> listTransfers(SelectionParameters params) {
        
        Triplet<List<TransferSO>, Long, Long> transfersData = getTransfersInternal(params);
        
        TableDataSO<TransferSO> result = new TableDataSO<>();
        result.setData(transfersData.getValue0());
        result.setCountTotal(Optional.ofNullable(
                transfersData.getValue1()).orElseGet(() -> transferRepo.count()));
        result.setCountFiltered(transfersData.getValue2());

        return result;
    }

    public TransferSO getTransfer(Long transferId) {
        return toSoMapper.map(findTransfer(transferId), new TransferSO());
    }
    
    private TransferEntity findTransfer(Long transferId) {
        TransferEntity transfer = transferRepo.findOne(transferId);
        if (transfer == null) {
            throw new BusinessException(ErrorCode.OBJECT_NOT_FOUND,
                    String.format("Transfer (id=%d) not found!", transferId));
        }
        
        return transfer;
    }
    
    
    @Transactional
    public synchronized TransferSO transferPlayer(Long playerId) {
        
        PlayerEntity player = playerRepo.findOne(playerId);
        if (player == null) {
            throw new BusinessException(ErrorCode.OBJECT_NOT_FOUND,
                    String.format("Player (id=%d) not found!", playerId));
        }
        if (player.isInTransfer() ||
                transferRepo.existsByPlayerAndFromTeamAndStatus(player, player.getTeam(), TransferStatus.OPEN)) {

            throw new BusinessException(ErrorCode.INVALID_STATE,
                    String.format("Player (id=%d) is already in transfer!", playerId));
        }
        
        player.setInTransfer(true);
        player = playerRepo.save(player);
        
        TransferEntity  transfer = transferRepo.save(new TransferEntity(player, player.getTeam()));
        
        LOGGER.info("Putting player (id={}) to transfer (id={}).", player.getId(), transfer.getId());
        
        return toSoMapper.map(transfer, new TransferSO());
    }

    @Transactional
    public void cancelTransfer(Long transferId) {
        
        TransferEntity transfer = transferRepo.findOne(transferId);
        if (transfer == null) {
            throw new BusinessException(ErrorCode.OBJECT_NOT_FOUND,
                    String.format("Transfer (id=%d) not found!", transferId));
        }
        
        transfer.setStatus(TransferStatus.CANCELED);
        transferRepo.save(transfer);
        
        PlayerEntity player = transfer.getPlayer();
        player.setInTransfer(false);
        playerRepo.save(player);
        
        LOGGER.info("Transfer (id={}) was canceled.", transfer.getId());
    }

}
