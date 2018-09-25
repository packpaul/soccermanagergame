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

$.Manager.pages.Teams = {

    $page: null,
    $teamsTable: null,
    
    editTeamId: null,
        
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
                {data: 'teamName', name: 'teamName'},
                {data: 'country', name: 'country'},
                {data: 'owner', name: 'owner'},
                {data: 'value', name: 'value', orderable: false},
                {data: 'balance', name: 'balance'},
                {data: 'creationDate', name: 'created'},
                {data: 'updateDate', name: 'updated'},
//                    {defaultContent: '', orderable: false},
                {data: null, orderable: false, searchable: false, render: function (info, type, row) {
                        var action = '<a href="#" onclick="$.Manager.pages.Teams.onEditTeam(' + info.id + ');">edit</a>';
                        if ($.Manager.userType != 'TEAM_OWNER') {
                            action += '<a href="#" onclick="$.Manager.pages.Teams.onDeleteTeam(' + info.id + ');"> delete</a>'
                        }
                        return action;
                    }
                }
            ]
        };
        
     
        if (! $.Manager.isPrototype) {
            $.extend(dtConfig, {
                ajax: function(data, callback, settings) {
                    $.rest.GET('/team/list' + createListQuery(data),
                        function(reqData) {
                            callback({
                                data: reqData.data,
                                draw: data.draw,
                                recordsTotal: reqData.countTotal,
                                recordsFiltered: reqData.countFiltered | reqData.countTotal
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
        
        this.$teamsTable = this.$page.find('#teamsTable').DataTable(dtConfig);

        this.initTeamsSearchBoxValues({
            teamName: this.$teamsTable.column('teamName:name').search(),
            country: this.$teamsTable.column('country:name').search(),
            owner: this.$teamsTable.column('owner:name').search()
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
    
    initTeamsSearchBoxValues: function(searchValues) {
        var $teamsSearchBox = this.$page.find('#teamsSearchBox');
        
        Object.keys(searchValues).forEach(function(name) {
            $teamsSearchBox.find('#' + name).val(searchValues[name]);
        });
        
        $teamsSearchBox.find('select').trigger("change");
    },
    
    getTeamsSearchBoxValues: function() {
        var values = {};
    
        var $teamsSearchBoxControls = this.$page.find('#teamsSearchBox .form-control');
        $teamsSearchBoxControls.each(function(index, $control) {
            values[$control.id] = $control.value.trim();
        }); 
        
        return values;
    },

    filterTable: function() {
        const self = this;
        const searchValues = self.getTeamsSearchBoxValues()
        
        console.info('Searching for: ', searchValues);
        
        Object.keys(searchValues).forEach(function(name) {
            self.$teamsTable.column(name + ':name').search(searchValues[name]);
        });

        this.$teamsTable.draw();
    },
    
    onEditTeam: function(teamId) {

        const self = this;
        self.setDataToEditTeamModal({});

        self.showEditTeamModal();
        
        if ($.Manager.isPrototype) {
            self.setDataToEditTeamModal({
                id: teamId,
                teamName: 'Soccer Dream Team',
                country: 'Mexica',
                owner: 'jose_owner',
                value: 20000000,
                balance: 5000000
            });
            
            return;
        }
        
        $.rest.GET(
            '/team/' + teamId,
            function(respData) {
                if (respData != undefined) {
//                    console.debug(respData);
                    self.setDataToEditTeamModal(respData);
                    self.editTeamId = respData.id;
                }
            }
        );
    },
    
    onDeleteTeam: function(teamId) {
        if (! confirm("Confirm deletion of team (id=" + teamId + ").")) {
            return;
        }
        
        if ($.Manager.isPrototype) {
            return;
        }
        
        $.rest.DELETE(
                '/team/' + teamId,
                function() {
                    self.$teamsTable.draw();
                },
                function(respData) {
                    alert(respData.error_description);
                }
            );
    },
    
    setDataToEditTeamModal: function(data) {
        this.find$editTeamModal().find('form').deserializeObject(data);
    },

    getDataFromEditTeamModal: function() {
        return this.find$editTeamModal().find('form').serializeObject();
    },
    
    showEditTeamModal: function() {
        this.find$editTeamModal().modal('show');  
    },
    
    hideEditTeamModal: function() {
        this.find$editTeamModal().modal('hide');
        this.editTeamId = null;  
    },
    
    find$editTeamModal: function() {
        return this.$page.find('#editTeamModal');
    },
    
    resetFilter: function() {
        var $teamsSearchBox = this.$page.find('#teamsSearchBox');
        $teamsSearchBox.find('.form-control').val('');
        $teamsSearchBox.find('select').trigger("change");
        this.filterTable();
    },
    
    saveTeam: function () {
        
        const self = this;
        const data = self.getDataFromEditTeamModal();
        
        console.info('updating team (id=' + self.editTeamId + '): ', data);
        
        if ($.Manager.isPrototype) {
            setTimeout(
                function() {
                    self.hideEditTeamModal();    
                },
                2000);
            return;
        }
        
        $.rest.PUT('/team/' + self.editTeamId, data,
            function(respData) {
//                console.debug(respData);
                self.hideEditTeamModal();
                self.$teamsTable.draw();
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
        $.Manager.pages.Teams.onLoad($body, null);
        $.Manager.pages.Teams.onShow($body, null);
    } else {
        $.Manager.pages.setHandlers(
            function($page, params) {
                $.Manager.pages.Teams.onLoad($page, params);
            },
            function($page, params) {
                $.Manager.pages.Teams.onShow($page, params);
            });
    } 
});