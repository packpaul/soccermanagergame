package com.pp.toptal.soccermanager.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.javatuples.Triplet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pp.toptal.soccermanager.entity.Country;
import com.pp.toptal.soccermanager.entity.PlayerEntity;
import com.pp.toptal.soccermanager.entity.QTransferEntity;
import com.pp.toptal.soccermanager.entity.TransferEntity;
import com.pp.toptal.soccermanager.entity.TransferEntity.TransferStatus;
import com.pp.toptal.soccermanager.exception.BusinessException;
import com.pp.toptal.soccermanager.exception.DataParameterException;
import com.pp.toptal.soccermanager.exception.ErrorCode;
import com.pp.toptal.soccermanager.mapper.EntityToSOMapper;
import com.pp.toptal.soccermanager.repo.OffsetBasedPageRequest;
import com.pp.toptal.soccermanager.repo.PlayerRepo;
import com.pp.toptal.soccermanager.repo.TransferRepo;
import com.pp.toptal.soccermanager.so.TableDataSO;
import com.pp.toptal.soccermanager.so.TransferSO;
import com.querydsl.core.types.ExpressionUtils;
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
        
        Predicate predicate = QTransferEntity.transferEntity.status.eq(TransferStatus.OPEN);
        
        if (params.getFilterProperties() != null) {
            String[] properties = params.getFilterProperties();
            String[] values = params.getFilterValues();
            QTransferEntity entity = QTransferEntity.transferEntity;
            for (int i = 0; i < properties.length; i++) {
                Predicate propPredicate;
                if (TransferSO.PLAYER_ID_PROP_NAME.equals(properties[i])) {
                    propPredicate = entity.player.id.eq(Long.valueOf(values[i]));
                } else if (TransferSO.PLAYER_COUNTRY_PROP_NAME.equals(properties[i])) {
                    Country value = Country.valueOf(values[i]);
                    propPredicate = entity.player.country.eq(value);                    
                } else if (TransferSO.FROM_TEAM_ID_PROP_NAME.equals(properties[i])) {
                    propPredicate = entity.fromTeam.id.eq(Long.valueOf(values[i]));
                } else if (TransferSO.FROM_TEAM_COUNTRY_PROP_NAME.equals(properties[i])) {
                    Country value = Country.valueOf(values[i]);
                    propPredicate = entity.fromTeam.country.eq(value);
                } else if (TransferSO.PLAYER_VALUE_PROP_NAME.equals(properties[i])) {
                    String value = values[i]; 
                    if (! value.matches("\\d*<=\\d*")) {
                        throw new DataParameterException(String.format(
                                "Filter for '%s' should be of form NUM<=NUM !", TransferSO.PLAYER_VALUE_PROP_NAME));
                    }
                    int p = value.indexOf("<=");
                    Long valueMin = Optional.of(value.substring(0, p))
                            .filter(StringUtils::isNotEmpty).map(Long::valueOf).orElse(null);
                    Long valueMax = Optional.of(value.substring(p + 2))
                            .filter(StringUtils::isNotEmpty).map(Long::valueOf).orElse(null);
                    propPredicate = ((valueMin != null) && (valueMax != null)) ? entity.player.value.between(valueMin, valueMax) :
                                    (valueMin != null) ? entity.player.value.goe(valueMin) : entity.player.value.loe(valueMax);
                } else {
                    continue;
                }
                predicate = (predicate != null) ? ExpressionUtils.and(predicate, propPredicate) :
                                                  propPredicate;
            }
        }

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
        
        TransferEntity transfer = findTransfer(transferId);
        if (transfer.getStatus() != TransferStatus.OPEN) {
            throw new BusinessException(ErrorCode.INVALID_STATE,
                    String.format("Transfer (id=%d) is not valid for cancelation!", transferId));            
        }
        
        transfer.setStatus(TransferStatus.CANCELED);
        transfer.setUpdateDate(new Date());
        transferRepo.save(transfer);
        
        PlayerEntity player = transfer.getPlayer();
        player.setInTransfer(false);
        playerRepo.save(player);
        
        LOGGER.info("Transfer (id={}) was canceled.", transfer.getId());
    }

}
