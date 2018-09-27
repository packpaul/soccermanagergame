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

$.Manager.pages.Proposals = {

    $page: null,
    $proposalsTable: null,
    
    editProposalId: null,
        
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
                {data: 'playerFullName', name: 'playerFullName', orderable: false},
                {data: 'playerType', name: 'playerType', orderable: false},
                {data: 'playerCountry', name: 'playerCountry', orderable: false},
                {data: 'toTeamName', name: 'toTeamName', orderable: false},
                {data: 'toTeamCountry', name: 'toTeamCountry', orderable: false},
                {data: 'playerValue', name: 'playerValue', orderable: false},
                {data: 'price', name: 'price', orderable: false},
                {data: 'creationDate', name: 'creationDate'},
//                    {defaultContent: '', orderable: false},
                {data: null, orderable: false, searchable: false, render: function (info, type, row) {
                        var action = '<a href="#" onclick="$.Manager.pages.Proposals.onAcceptProposal(' + info.id + ');"> accept</a>';
                        action += '<a href="#" onclick="$.Manager.pages.Proposals.onCancelProposal(' + info.id + ');"> cancel</a>'
                        action += '<a href="#" onclick="' + "$.Manager.onMessageUser('" + info.toTeamOwnerUsername + "');" + '"> ask</a>'
                        return action;
                    }
                }
            ]
        };
     
        if (! $.Manager.isPrototype) {
            $.extend(dtConfig, {
                ajax: function(data, callback, settings) {
                    $.rest.GET('/proposal/list' + createListQuery(data),
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
        
        this.$proposalsTable = this.$page.find('#proposalsTable').DataTable(dtConfig);

        this.initProposalsSearchBoxValues({
            playerFullName: this.$proposalsTable.column('playerFullName:name').search(),
            creationDateFrom: this.$proposalsTable.column('').search(),
            creationDateTill: this.$proposalsTable.column('').search()
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
    
    initProposalsSearchBoxValues: function(searchValues) {
        var $proposalsSearchBox = this.$page.find('#proposalsSearchBox');
        
        Object.keys(searchValues).forEach(function(name) {
            $proposalsSearchBox.find('#' + name).val(searchValues[name]);
        });
        
        $proposalsSearchBox.find('select').trigger("change");
    },
    
    getProposalsSearchBoxValues: function() {
        var values = {};
    
        var $proposalsSearchBoxControls = this.$page.find('#proposalsSearchBox .form-control');
        $proposalsSearchBoxControls.each(function(index, $control) {
            values[$control.id] = $control.value.trim();
        }); 
        
        return values;
    },

    filterTable: function() {
        const self = this;
        const searchValues = self.getProposalsSearchBoxValues()
        
        console.info('Searching for: ', searchValues);
        
        Object.keys(searchValues).forEach(function(name) {
            self.$proposalsTable.column(name + ':name').search(searchValues[name]);
        });

        this.$proposalsTable.draw();
    },
    
    resetFilter: function() {
        var $proposalsSearchBox = this.$page.find('#proposalsSearchBox');
        $proposalsSearchBox.find('.form-control').val('');
        $proposalsSearchBox.find('select').trigger("change");
        this.filterTable();
    },

    onCancelProposal: function(proposalId) {
        if (! confirm("Confirm cancelation of proposal (id=" + proposalId + ").")) {
            return;
        }
        
        if ($.Manager.isPrototype) {
            return;
        }
        
        const self = this;
        
        $.rest.POST(
            '/proposal/' + proposalId + '/cancel',
            null,
            function() {
                self.$proposalsTable.draw();
            },
            function(status, respData) {
                alert(respData.error_description);
            }
        );
    },
    
    onAcceptProposal: function(proposalId) {
        if (! confirm("You are going to accept proposal (id=" + proposalId + "). Please confirm it!")) {
            return;
        }
        
        if ($.Manager.isPrototype) {
            return;
        }
        
        const self = this;
        
        $.rest.POST(
            '/proposal/' + proposalId + '/accept',
            null,
            function() {
                self.$proposalsTable.draw();
            },
            function(status, respData) {
                alert(respData.error_description);
            }
        );
    }
    
};

$(function() {
    if ($.Manager.isPrototype) {
        var $body = $(document.body);
        $.Manager.pages.Proposals.onLoad($body, null);
        $.Manager.pages.Proposals.onShow($body, null);
    } else {
        $.Manager.pages.setHandlers(
            function($page, params) {
                $.Manager.pages.Proposals.onLoad($page, params);
            },
            function($page, params) {
                $.Manager.pages.Proposals.onShow($page, params);
            });
    } 
});