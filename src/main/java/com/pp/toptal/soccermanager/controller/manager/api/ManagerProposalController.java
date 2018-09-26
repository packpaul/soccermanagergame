package com.pp.toptal.soccermanager.controller.manager.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pp.toptal.soccermanager.service.SelectionParameters;
import com.pp.toptal.soccermanager.service.ProposalService;
import com.pp.toptal.soccermanager.so.NullSO;
import com.pp.toptal.soccermanager.so.TableDataSO;
import com.pp.toptal.soccermanager.so.ProposalSO;

@RestController()
@RequestMapping(ManagerApi.ROOT + "/v" + ManagerApi.BASELINE_VERSION + "/proposal")
public class ManagerProposalController {

    @Autowired
    private ProposalService proposalService;

    @GetMapping
    public List<ProposalSO> getProposals(
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
        
        return proposalService.getProposals(params);
    }
    
    @GetMapping("/list")
    public TableDataSO<ProposalSO> listProposals(
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
        
        return proposalService.listProposals(params);
    }

    @GetMapping("/{proposalId}")
    public ProposalSO getProposal(@PathVariable("proposalId") Long proposalId) {
          
        return proposalService.getProposal(proposalId);
    }

    @PostMapping("/transfer/{transferId}")
    public ProposalSO createTransferProposal(
            @PathVariable("transferId") Long transferId,
            @RequestBody ProposalSO proposal) {
          
        return proposalService.createProposal(transferId, proposal.getToTeamId(), proposal.getPrice());
    } 
    
    @PostMapping("/{proposalId}/cancel")
    public NullSO cancelProposal(
            @PathVariable("proposalId") Long proposalId) {
          
        proposalService.removeProposal(proposalId);
        
        return NullSO.INSTANCE;
    }
    
    @PostMapping("/{proposalId}/accept")
    public NullSO acceptProposal(
            @PathVariable("proposalId") Long proposalId) {
          
        proposalService.acceptProposal(proposalId);
        
        return NullSO.INSTANCE;
    }

}