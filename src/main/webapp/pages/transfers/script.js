'use strict';

if (typeof jQuery == "undefined") {
    throw new Error("jQuery is required!");
}

if (! $.Manager) {
    $.Manager = {
        pages: {},
        isPrototype: true,
        onMessageUser: function(toUser) {
            var message = prompt("Ask user '" + toUser + "':", "I'd like to have ...");
        },
        select2: {
            players: function($select) {
                $select.select2();
            },
            teams: function($select) {
                $select.select2();
            }
        }
    }
}

$.Manager.pages.Transfers = {

    $page: null,
    $transfersTable: null,
    
    editTransferId: null,
        
    onLoad: function($page) {
    
        this.$page = $page;
        
        $page.find("[data-mask]").inputmask(undefined, { rightAlignNumerics: false });
        
        var dtConfig = {
            processing: true,
            select: true,
            iDisplayLength: 10,
            dom: 'lrtip',
            search: {},

            columns: [
                {data: 'id', name: 'id'},
                {data: 'playerId', name: 'playerId', orderable: false, visible: false},
                {data: 'playerFullName', name: 'playerFullName', orderable: false},
                {data: 'playerType', name: 'playerType', orderable: false},
                {data: 'playerAge', name: 'playerAge', orderable: false},
                {data: 'playerCountry', name: 'playerCountry', orderable: false},
                {data: 'fromTeamId', name: 'fromTeamId', orderable: false, visible: false},
                {data: 'fromTeamName', name: 'fromTeamName', orderable: false},
                {data: 'fromTeamCountry', name: 'fromTeamCountry', orderable: false},
                {data: 'playerValue', name: 'playerValue', orderable: false},
                {data: 'creationDate', name: 'creationDate'},
//                    {defaultContent: '', orderable: false},
                {data: null, orderable: false, searchable: false, render: function (info, type, row) {
                        var action = '';
                        if (($.Manager.userType != 'TEAM_OWNER') || (info.fromTeamOwnerUserId != $.Manager.userId)) {
                            action = '<a href="#" onclick="$.Manager.pages.Transfers.onDoTransferProposal(' + info.id + ');"> propose</a>';
                            action += '<a href="#" onclick="' + "$.Manager.onMessageUser('" + info.fromTeamOwnerUsername + "');" + '"> ask</a>'
                        }
                        action += '<a href="#" onclick="$.Manager.pages.Transfers.onCancelTransfer(' + info.id + ');"> cancel</a>'
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
            playerId: this.$transfersTable.column('playerId:name').search(),
            playerCountry: this.$transfersTable.column('playerCountry:name').search(),
            fromTeamId: this.$transfersTable.column('fromTeamId:name').search(),
            fromTeamCountry: this.$transfersTable.column('fromTeamCountry:name').search(),
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
        if (! $page.prop('shown')) {
            $page.prop('shown', true);
            $.Manager.select2.players(this.find$transfersSearchBox().find('#playerId'));
            $.Manager.select2.teams(this.find$transfersSearchBox().find('#fromTeamId'));
        }
    },
    
    find$transfersSearchBox: function() {
        return this.$page.find('#transfersSearchBox'); 
    },
    
    initTransfersSearchBoxValues: function(searchValues) {
        var $transfersSearchBox = this.find$transfersSearchBox();
        
        Object.keys(searchValues).forEach(function(name) {
            $transfersSearchBox.find('#' + name).val(searchValues[name]);
        });
        
        $transfersSearchBox.find('select').trigger("change");
    },
    
    getTransfersSearchBoxValues: function() {
        var values = {};
    
        var $transfersSearchBoxControls = this.find$transfersSearchBox().find('.form-control');
        $transfersSearchBoxControls.each(function(index, $control) {
            values[$control.id] = $control.value.trim();
        });
        
        values.playerValue = (values.playerValueMin || values.playerValueMax) ?
                (values.playerValueMin || '') + '<=' + (values.playerValueMax || '') : '';
        
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
                    respData.proposalPrice = respData.playerValue;
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
            null,
            function() {
                self.$transfersTable.draw();
            },
            function(status, respData) {
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
        var $transferProposalModal = this.find$transferProposalModal(); 

        $transferProposalModal.modal('show');
        
        if (! $transferProposalModal.prop('shown')) {
            $transferProposalModal.prop('shown', true)
            $.Manager.select2.teams($transferProposalModal.find('#toTeamId'));
        }
        
        this.find$transferProposalModal().find('select').trigger("change");
    },
    
    hideTransferProposalModal: function() {
        this.find$transferProposalModal().modal('hide');
        this.editTransferId = null;  
    },
    
    find$transferProposalModal: function() {
        return this.$page.find('#transferProposalModal');
    },
    
    resetFilter: function() {
        var $transfersSearchBox = this.find$transfersSearchBox();
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
        
        $.rest.POST('/proposal/transfer/' + self.editTransferId,
            {
                price: data.proposalPrice,
                toTeamId: data.toTeamId
            },
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