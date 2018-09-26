package com.pp.toptal.soccermanager.controller.manager.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pp.toptal.soccermanager.service.SelectionParameters;
import com.pp.toptal.soccermanager.service.TransferService;
import com.pp.toptal.soccermanager.so.NullSO;
import com.pp.toptal.soccermanager.so.TableDataSO;
import com.pp.toptal.soccermanager.so.TransferSO;

@RestController()
@RequestMapping(ManagerApi.ROOT + "/v" + ManagerApi.BASELINE_VERSION + "/transfer")
public class ManagerTransferController {

    @Autowired
    private TransferService transferService;

    @GetMapping
    public List<TransferSO> getTransfers(
            @RequestParam(name = "filterProperties[]", required = false) String[] filterProperties,
            @RequestParam(name = "filterValues[]", required = false) String[] filterValues,
            @RequestParam(name = "orderProperty", required = false) String orderProperty,
            @RequestParam(name = "orderDir", required = false) String orderDir,
            @RequestParam(name = "offset", required = false) Long offset,
            @RequestParam(name = "limit", required = false) Long limit) {

        SelectionParameters params = new SelectionParameters();
        if ((filterProperties != null) && (filterValues != null)) {
            params.filters(filterProperties, filterValues);            
        }
        if ((orderProperty != null) && (orderDir != null)) {
            params.order(orderProperty, orderDir);            
        }
        params.offset(offset).limit(limit);
        
        return transferService.getTransfers(params);
    }
    
    @GetMapping("/list")
    public TableDataSO<TransferSO> listTransfers(
            @RequestParam(name = "filterProperties[]", required = false) String[] filterProperties,
            @RequestParam(name = "filterValues[]", required = false) String[] filterValues,
            @RequestParam(name = "orderProperty", required = false) String orderProperty,
            @RequestParam(name = "orderDir", required = false) String orderDir,
            @RequestParam(name = "offset", required = false) Long offset,
            @RequestParam(name = "limit", required = false) Long limit) {
        
        SelectionParameters params = new SelectionParameters();
        if ((filterProperties != null) && (filterValues != null)) {
            params.filters(filterProperties, filterValues);            
        }
        if ((orderProperty != null) && (orderDir != null)) {
            params.order(orderProperty, orderDir);            
        }
        params.offset(offset).limit(limit);
        
        return transferService.listTransfers(params);
    }

    @GetMapping("/{transferId}")
    public TransferSO getTransfer(@PathVariable("transferId") Long transferId) {
          
        return transferService.getTransfer(transferId);
    }
    
    @PostMapping("/player/{playerId}")
    public TransferSO transferPlayer(
            @PathVariable("playerId") Long playerId) {
          
        return transferService.transferPlayer(playerId);
    } 
    
    @PostMapping("/{transferId}/cancel")
    public NullSO cancelTransfer(
            @PathVariable("transferId") Long transferId) {
          
        transferService.cancelTransfer(transferId);
        
        return NullSO.INSTANCE;
    }

}