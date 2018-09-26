'use strict';

if (typeof jQuery == "undefined") {
    throw new Error("jQuery is required!");
}

if (! $.Manager) {
    $.Manager = {
        pages: {},
        isPrototype: true
    }
}

$.Manager.pages.Transfers = {

    $page: null,
    $transfersTable: null,
    
    editTransferId: null,
        
    onLoad: function($page) {
    
        this.$page = $page;
        
        var dtConfig = {
            processing: true,
            select: true,
            iDisplayLength: 10,
            dom: 'lrtip',
            search: {},

            columns: [
                {data: 'id', name: 'id'},
                {data: 'playerFullName', name: 'playerFullName'},
                {data: 'playerType', name: 'playerType'},
                {data: 'playerAge', name: 'playerAge'},
                {data: 'playerCountry', name: 'playerCountry'},
                {data: 'fromTeamName', name: 'fromTeamName'},
                {data: 'fromTeamCountry', name: 'fromTeamCountry'},
                {data: 'playerValue', name: 'playerValue'},
                {data: 'creationDate', name: 'creationDate'},
//                    {defaultContent: '', orderable: false},
                {data: null, orderable: false, searchable: false, render: function (info, type, row) {
                        var action = '<a href="#" onclick="$.Manager.pages.Transfers.onDoTransferProposal(' + info.id + ');"> propose</a>';
                        action += '<a href="#" onclick="$.Manager.pages.Transfers.onCancelTransfer(' + info.id + ');"> cancel</a>'
                        action += '<a href="#" onclick="$.Manager.onAskUser(' + info.teamOwnerId + ');"> ask</a>'
                        return action;
                    }
                }
            ]
        };
     
        if (! $.Manager.isPrototype) {
            $.extend(dtConfig, {
                ajax: function(data, callback, settings) {
                    $.rest.GET('/transfer/list' + createListQuery(data),
                        function(reqData) {
                            callback({
                                data: reqData.data,
                                draw: data.draw,
                                recordsTotal: reqData.countTotal,
                                recordsFiltered: reqData.countFiltered || reqData.countTotal
                            });
                        },
                        function(status, reqData) {
//                            console.debug(status);
                            if (reqData.error !== 'invalid_data') {
                                return;
                            }
                            callback({
                                data: [],
                                draw: data.draw,
                                recordsTotal: 0,
                                recordsFiltered: 0,
                                error: reqData.error_description
                            });
                        }
                    );
                },
                serverSide: true,
                stateSave: false
            });
        }
        
        this.$transfersTable = this.$page.find('#transfersTable').DataTable(dtConfig);

        this.initTransfersSearchBoxValues({
            playerType: this.$transfersTable.column('playerType:name').search(),
            playerCountry: this.$transfersTable.column('playerCountry:name').search(),
            playerValueMin: this.$transfersTable.column('').search(),
            playerValueMax: this.$transfersTable.column('').search(),
        });
        
        function createListQuery(data) {
//            console.debug(data);
        
            var query = $.query.empty();
            
            var filterProperties = [];
            var filterValues = [];
            data.columns.forEach(function(column) {
                if (column.searchable && column.search.value) {
                    filterProperties.push(column.data);
                    filterValues.push(column.search.value);
                }
            });
            if (filterProperties.length) {
                query = query.set('filterProperties', filterProperties);
                query = query.set('filterValues', filterValues);
            }

            if (data.order.length) {
                query = query.set('orderProperty', data.columns[data.order[0].column].data);
                query = query.set('orderDir', data.order[0].dir);
            }
            
            if (data.start) {
                query = query.set('offset', data.start);
            }
            if (data.length) {
                query = query.set('limit', data.length);
            }
            
            return query.toString();
        }
    },
        
    onShow: function($page, params) {
//      $page.find("select").select2();
    },
    
    initTransfersSearchBoxValues: function(searchValues) {
        var $transfersSearchBox = this.$page.find('#transfersSearchBox');
        
        Object.keys(searchValues).forEach(function(name) {
            $transfersSearchBox.find('#' + name).val(searchValues[name]);
        });
        
        $transfersSearchBox.find('select').trigger("change");
    },
    
    getTransfersSearchBoxValues: function() {
        var values = {};
    
        var $transfersSearchBoxControls = this.$page.find('#transfersSearchBox .form-control');
        $transfersSearchBoxControls.each(function(index, $control) {
            values[$control.id] = $control.value.trim();
        }); 
        
        return values;
    },

    filterTable: function() {
        const self = this;
        const searchValues = self.getTransfersSearchBoxValues()
        
        console.info('Searching for: ', searchValues);
        
        Object.keys(searchValues).forEach(function(name) {
            self.$transfersTable.column(name + ':name').search(searchValues[name]);
        });

        this.$transfersTable.draw();
    },
    
    onDoTransferProposal: function(transferId) {

        const self = this;
        self.setDataToTransferProposalModal({});

        self.showTransferProposalModal();
        
        if ($.Manager.isPrototype) {
            self.setDataToTransferProposalModal({
                id: transferId,
                playerFullName: 'Jordi di María',
                playerType: 'ATTACKER',
                playerAge: 28,
                playerCountry: 'BRAZIL',
                playerValue: 1450000,
                proposalPrice: 1500000
            });
            
            return;
        }
        
        $.rest.GET(
            '/transfer/' + transferId,
            function(respData) {
                if (respData != undefined) {
//                    console.debug(respData);
                    respData.proposalPrice = playerValue;
                    self.setDataToTransferProposalModal(respData);
                    self.editTransferId = respData.id;
                }
            }
        );
    },
    
    onCancelTransfer: function(transferId) {
        if (! confirm("Confirm cancelation of transfer (id=" + transferId + ").")) {
            return;
        }
        
        if ($.Manager.isPrototype) {
            return;
        }
        
        const self = this;
        
        $.rest.POST(
                '/transfer/' + transferId + '/cancel',
                function() {
                    self.$transfersTable.draw();
                },
                function(respData) {
                    alert(respData.error_description);
                }
            );
    },
    
    setDataToTransferProposalModal: function(data) {
        var $transferProposalModalForm = this.find$transferProposalModal().find('form');
        $transferProposalModalForm.deserializeObject(data);
    },

    getDataFromTransferProposalModal: function() {
        return this.find$transferProposalModal().find('form').serializeObject();
    },
    
    showTransferProposalModal: function() {
        this.find$transferProposalModal().modal('show');  
    },
    
    hideTransferProposalModal: function() {
        this.find$transferProposalModal().modal('hide');
        this.editTransferId = null;  
    },
    
    find$transferProposalModal: function() {
        return this.$page.find('#transferProposalModal');
    },
    
    resetFilter: function() {
        var $transfersSearchBox = this.$page.find('#transfersSearchBox');
        $transfersSearchBox.find('.form-control').val('');
        $transfersSearchBox.find('select').trigger("change");
        this.filterTable();
    },
    
    doTransferProposal: function () {
        
        const self = this;
        const data = self.getDataFromTransferProposalModal();
        
        console.info('updating transfer (id=' + self.editTransferId + '): ', data);
        
        if ($.Manager.isPrototype) {
            setTimeout(
                function() {
                    self.hideTransferProposalModal();    
                },
                2000);
            return;
        }
        
        $.rest.PUT('/transfer/' + self.editTransferId, data,
            function(respData) {
//                console.debug(respData);
                self.hideTransferProposalModal();
                self.$transfersTable.draw();
            },
            function(status, respData) {
                if (respData) {
                    alert(respData.error_description);
                }
            }
        );
    }
    
};

$(function() {
    if ($.Manager.isPrototype) {
        var $body = $(document.body);
        $.Manager.pages.Transfers.onLoad($body, null);
        $.Manager.pages.Transfers.onShow($body, null);
    } else {
        $.Manager.pages.setHandlers(
            function($page, params) {
                $.Manager.pages.Transfers.onLoad($page, params);
            },
            function($page, params) {
                $.Manager.pages.Transfers.onShow($page, params);
            });
    } 
});