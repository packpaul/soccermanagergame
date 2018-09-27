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

$.Manager.pages.Players = {

    $page: null,
    $playersTable: null,
    
    editPlayerId: null,
        
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
                {data: 'firstName', name: 'firstName'},
                {data: 'lastName', name: 'lastName'},
                {data: 'playerType', name: 'playerType'},
                {data: 'age', name: 'age'},
                {data: 'country', name: 'country'},
                {data: 'teamId', name: 'teamId', visible: false},
                {data: 'teamName', name: 'teamName', orderable: false},
                {data: 'teamCountry', name: 'teamCountry', orderable: false},
                {data: 'value', name: 'value'},
                {data: 'inTransfer', name: 'inTransfer', searchable: false, render: function(info, type, row) {
                        return info ? 'yes' : '';
                    }
                },
//                {data: 'creationDate', name: 'created'},
//                {data: 'updateDate', name: 'updated'},
//                    {defaultContent: '', orderable: false},
                {data: null, orderable: false, searchable: false, render: function (info, type, row) {
                        var action = '<a href="#" onclick="$.Manager.pages.Players.onInfoPlayer(' + info.id + ');"> info</a>';
                        action += '<a href="#" onclick="$.Manager.pages.Players.onTransferPlayer(' + info.id + ');"> transfer</a>'
                        if ((! $.Manager.userType) || ($.Manager.userType != 'TEAM_OWNER')) {
                            action += '<a href="#" onclick="$.Manager.pages.Players.onEditPlayer(' + info.id + ');"> edit</a>';
                            action += '<a href="#" onclick="$.Manager.pages.Players.onDeletePlayer(' + info.id + ');"> delete</a>'
                        }
                        return action;
                    }
                }
            ]
        };
     
        if (! $.Manager.isPrototype) {
            $.extend(dtConfig, {
                ajax: function(data, callback, settings) {
                    $.rest.GET('/player/list' + createListQuery(data),
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
        
        this.$playersTable = this.$page.find('#playersTable').DataTable(dtConfig);

        this.initPlayersSearchBoxValues({
            firstName: this.$playersTable.column('firstName:name').search(),
            lastName: this.$playersTable.column('lastName:name').search(),
            playerType: this.$playersTable.column('playerType:name').search(),
            country: this.$playersTable.column('country:name').search(),
            country: this.$playersTable.column('teamId:name').search()
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
        
        const searchValues = this.getPlayersSearchBoxValues();
        
        var teamId = (params) ? $.query.load('?' + params).get('teamId') : null;
        if (searchValues.teamId != teamId) {
            searchValues.teamId = teamId;
            this.initPlayersSearchBoxValues(searchValues);
            this.filterTable();
        }
    },
    
    initPlayersSearchBoxValues: function(searchValues) {
        var $playersSearchBox = this.$page.find('#playersSearchBox');
        
        Object.keys(searchValues).forEach(function(name) {
            $playersSearchBox.find('#' + name).val(searchValues[name]);
        });
        
        $playersSearchBox.find('select').trigger("change");
    },
    
    getPlayersSearchBoxValues: function() {
        var values = {};
    
        var $playersSearchBoxControls = this.$page.find('#playersSearchBox .form-control');
        $playersSearchBoxControls.each(function(index, $control) {
            values[$control.id] = $control.value.trim();
        }); 
        
        return values;
    },

    filterTable: function() {
        const self = this;
        const searchValues = self.getPlayersSearchBoxValues()
        
        console.info('Searching for: ', searchValues);
        
        Object.keys(searchValues).forEach(function(name) {
            self.$playersTable.column(name + ':name').search(searchValues[name]);
        });

        this.$playersTable.draw();
    },
    
    onInfoPlayer: function(playerId) {
        const self = this;
        self.setDataToEditPlayerModal({});

        self.showEditPlayerModal('info');
        
        if ($.Manager.isPrototype) {
            self.setDataToEditPlayerModal({
                id: playerId,
                firstName: 'Jordi',
                lastName: 'di María',
                playerType: 'ATTACKER',
                age: 28,
                country: 'BRAZIL',
                teamId: 1,
                value: 1450000
            });
            
            self.editPlayerId = playerId;
            
            return;
        }
        
        $.rest.GET(
            '/player/' + playerId,
            function(respData) {
                if (respData != undefined) {
//                    console.debug(respData);
                    self.setDataToEditPlayerModal(respData);
                    self.editPlayerId = respData.id;
                }
            }
        );
    },
    
    onAddPlayer: function() {

        const self = this;
        self.setDataToEditPlayerModal({
            value: 0
        });

        self.editPlayerId = null;
        self.showEditPlayerModal('add');
    },
    
    onEditPlayer: function(playerId) {

        const self = this;
        self.setDataToEditPlayerModal({});

        self.showEditPlayerModal();
        
        if ($.Manager.isPrototype) {
            self.setDataToEditPlayerModal({
                id: playerId,
                firstName: 'Jordi',
                lastName: 'di María',
                playerType: 'ATTACKER',
                age: 28,
                country: 'BRAZIL',
                teamId: 1,
                value: 1450000
            });
            
            self.editPlayerId = playerId;
            
            return;
        }
        
        $.rest.GET(
            '/player/' + playerId,
            function(respData) {
                if (respData != undefined) {
//                    console.debug(respData);
                    self.setDataToEditPlayerModal(respData);
                    self.editPlayerId = respData.id;
                }
            }
        );
    },
    
    onDeletePlayer: function(playerId) {
        if (! confirm("Confirm deletion of player (id=" + playerId + ").")) {
            return;
        }
        
        if ($.Manager.isPrototype) {
            return;
        }
        
        const self = this;
        
        $.rest.DELETE(
                '/player/' + playerId,
                function() {
                    self.$playersTable.draw();
                },
                function(respData) {
                    alert(respData.error_description);
                }
            );
    },
    
    onTransferPlayer: function(playerId) {
        if (! confirm("Confirm transferring of player (id=" + playerId + ").")) {
            return;
        }
        
        if ($.Manager.isPrototype) {
            return;
        }
        
        const self = this;
        
        $.rest.POST(
                '/transfer/player/' + playerId,
                null,
                function() {
                    self.$playersTable.draw();
                },
                function(status, respData) {
                    alert(respData.error_description);
                }
            );
    },
    
    setDataToEditPlayerModal: function(data) {
        var $editPlayerModalForm = this.find$editPlayerModal().find('form');
        $editPlayerModalForm.deserializeObject(data);
    },

    getDataFromEditPlayerModal: function() {
        return this.find$editPlayerModal().find('form').serializeObject();
    },
    
    showEditPlayerModal: function(modalType) {
        
        var $editPlayerModal = this.find$editPlayerModal();
        var $editPlayerModalTitle = $editPlayerModal.find('.modal-title');
        var $editPlayerModalSave = $editPlayerModal.find('#saveSettingButton');
        
        $editPlayerModalTitle.text('Edit Player');
        $editPlayerModalSave.removeClass('hidden');
//        $editPlayerModal.enableInput();
        
        if (modalType == 'info') {
            $editPlayerModalTitle.text('Player Info');
            $editPlayerModalSave.addClass('hidden');
//            $editPlayerModal.disableInput();
        } else if (modalType == 'add') {
            $editPlayerModalTitle.text('Add Player');
        }

        $editPlayerModal.modal('show');  
    },
    
    hideEditPlayerModal: function() {
        this.find$editPlayerModal().modal('hide');
        this.editPlayerId = null;  
    },
    
    find$editPlayerModal: function() {
        return this.$page.find('#editPlayerModal');
    },
    
    resetFilter: function() {
        var $playersSearchBox = this.$page.find('#playersSearchBox');
        $playersSearchBox.find('.form-control').val('');
        $playersSearchBox.find('select').trigger("change");
        this.filterTable();
    },
    
    savePlayer: function () {
        
        const self = this;
        const data = self.getDataFromEditPlayerModal();
        
        if (self.editPlayerId) {
            console.info('updating player (id=' + self.editPlayerId + '): ', data);            
        } else {
            console.info('adding player: ', data);
        }
        
        if ($.Manager.isPrototype) {
            setTimeout(
                function() {
                    self.hideEditPlayerModal();    
                },
                2000);
            return;
        }
        
        if (self.editPlayerId) {
            $.rest.PUT('/player/' + self.editPlayerId,
                data,
                function(respData) {
//                    console.debug(respData);
                    self.hideEditPlayerModal();
                    self.$playersTable.draw(); // TODO: update single row in the table
                },
                function(status, respData) {
                    if (respData) {
                        alert(respData.error_description);
                    }
                }
            );
        } else {
            $.rest.POST('/player/',
                data,
                function(respData) {
//                    console.debug(respData);
                    self.hideEditPlayerModal();
                    self.$playersTable.draw(); // TODO: insert single row into the table
                },
                function(status, respData) {
                    if (respData) {
                        alert(respData.error_description);
                    }
                }
            );
        }

    }
    
};

$(function() {
    if ($.Manager.isPrototype) {
        var $body = $(document.body);
        $.Manager.pages.Players.onLoad($body, null);
        $.Manager.pages.Players.onShow($body, null);
    } else {
        $.Manager.pages.setHandlers(
            function($page, params) {
                $.Manager.pages.Players.onLoad($page, params);
            },
            function($page, params) {
                $.Manager.pages.Players.onShow($page, params);
            });
    } 
});