package com.pp.toptal.soccermanager.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.javatuples.Triplet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.pp.toptal.soccermanager.entity.QUserEntity;
import com.pp.toptal.soccermanager.entity.UserEntity;
import com.pp.toptal.soccermanager.exception.BusinessException;
import com.pp.toptal.soccermanager.exception.DataParameterException;
import com.pp.toptal.soccermanager.exception.ErrorCode;
import com.pp.toptal.soccermanager.mapper.EntityToSOMapper;
import com.pp.toptal.soccermanager.repo.OffsetBasedPageRequest;
import com.pp.toptal.soccermanager.repo.UserRepo;
import com.pp.toptal.soccermanager.so.TableDataSO;
import com.pp.toptal.soccermanager.so.UserSO;
import com.querydsl.core.types.Predicate;

@Service
public class UserService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private UserRepo userRepo;
    
    @Autowired
    private EntityToSOMapper toSoMapper;

    public List<UserSO> getUsers(SelectionParameters params) {
        return getUsersInternal(params).getValue0();
    }
    
    public Triplet<List<UserSO>, Long, Long> getUsersInternal(SelectionParameters params) {
        
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
        if (params.getFilterProperties() != null) {
            String[] properties = params.getFilterProperties();
            String[] values = params.getFilterValues();
            QUserEntity entity = QUserEntity.userEntity;
            for (int i = 0; i < properties.length; i++) {
                if (Objects.equals(properties[i], entity.username.getMetadata().getName())) {
                    String value = values[i];
                    if (! value.matches("[\\w\\*]+")) {
                        throw new DataParameterException("Filter for 'username' should only contain [a-zA-Z_*] !");
                    }
                    value = value.replace('*', '%');
                    predicate = entity.username.likeIgnoreCase(value);
                }
            }
        }
        
        final long count = userRepo.count(predicate);
        
        List<UserSO> users = new ArrayList<>(Math.min(limit, (int) count));

        Iterable<UserEntity> ui = userRepo.findAll(predicate, pageReq);
        for (UserEntity u : ui) {
            users.add(toSoMapper.map(u, new UserSO()));
        }

        
        
        if (predicate != null) {
            return Triplet.with(users, null, count);
        }
        
        return Triplet.with(users, count, count);
    }

    public TableDataSO<UserSO> listUsers(SelectionParameters params) {
        
        Triplet<List<UserSO>, Long, Long> usersData = getUsersInternal(params);
        
        TableDataSO<UserSO> result = new TableDataSO<>();
        result.setData(usersData.getValue0());
        result.setCountTotal(Optional.ofNullable(
                usersData.getValue1()).orElseGet(() -> userRepo.count()));
        result.setCountFiltered(usersData.getValue2());

        return result;
    }

    public UserSO getUser(Long userId) {
        UserEntity user = userRepo.findOne(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.OBJECT_NOT_FOUND,
                    String.format("Users (id=%d) not found!", userId));
        }
        
        return toSoMapper.map(user, new UserSO());
    }
    
}
