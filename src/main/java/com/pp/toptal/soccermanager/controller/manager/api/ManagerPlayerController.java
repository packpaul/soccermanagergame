package com.pp.toptal.soccermanager.controller.manager.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pp.toptal.soccermanager.service.SelectionParameters;
import com.pp.toptal.soccermanager.service.PlayerService;
import com.pp.toptal.soccermanager.so.NullSO;
import com.pp.toptal.soccermanager.so.TableDataSO;
import com.pp.toptal.soccermanager.so.PlayerSO;

@RestController()
@RequestMapping(ManagerApi.ROOT + "/v" + ManagerApi.BASELINE_VERSION + "/player")
public class ManagerPlayerController {

    @Autowired
    private PlayerService playerService;

    @GetMapping
    public List<PlayerSO> getPlayers(
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
        
        return playerService.getPlayers(params);
    }
    
    @GetMapping("/list")
    public TableDataSO<PlayerSO> listPlayers(
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
        
        return playerService.listPlayers(params);
    }

    @GetMapping("/{playerId}")
    public PlayerSO getPlayer(@PathVariable("playerId") Long playerId) {
          
        return playerService.getPlayer(playerId);
    }
    
    @PutMapping("/{playerId}")
    public PlayerSO updatePlayer(
            @PathVariable("playerId") Long playerId, @RequestBody PlayerSO playerData) {
          
        return  playerService.updatePlayer(playerId, playerData);
    }
    
    @DeleteMapping("/{playerId}")
    public NullSO deletePlayer(
            @PathVariable("playerId") Long playerId) {
          
        playerService.deletePlayer(playerId);
        
        return NullSO.INSTANCE;
    }

}