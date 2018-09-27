package com.pp.toptal.soccermanager.controller.manager.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pp.toptal.soccermanager.service.SelectionParameters;
import com.pp.toptal.soccermanager.service.TeamService;
import com.pp.toptal.soccermanager.so.NullSO;
import com.pp.toptal.soccermanager.so.TableDataSO;
import com.pp.toptal.soccermanager.so.TeamSO;

@RestController()
@RequestMapping(ManagerApi.ROOT + "/v" + ManagerApi.BASELINE_VERSION + "/team")
public class ManagerTeamController {

    @Autowired
    private TeamService teamService;

    @GetMapping
    public List<TeamSO> getTeams(
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
        
        return teamService.getTeams(params);
    }
    
    @GetMapping("/list")
    public TableDataSO<TeamSO> listTeams(
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
        
        return teamService.listTeams(params);
    }

    @GetMapping("/{teamId}")
    public TeamSO getTeam(@PathVariable("teamId") Long teamId) {
          
        return teamService.getTeam(teamId);
    }

    @PostMapping
    public TeamSO addTeam(@RequestBody TeamSO teamData) {
          
        return teamService.addTeam(teamData);
    }
    
    @PutMapping("/{teamId}")
    public TeamSO updateTeam(
            @PathVariable("teamId") Long teamId, @RequestBody TeamSO teamData) {
          
        return teamService.updateTeam(teamId, teamData);
    }
    
    @DeleteMapping("/{teamId}")
    public NullSO deleteTeam(
            @PathVariable("teamId") Long teamId) {
          
        teamService.deleteTeam(teamId);
        
        return NullSO.INSTANCE;
    }

}