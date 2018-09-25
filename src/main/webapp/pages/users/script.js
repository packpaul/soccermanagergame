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

$.Manager.pages.Users = {

    $page: null,
    $table: null,
    
    editUserId: null,
        
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
                {data: 'username', name: 'username'},
                {data: 'userType', name: 'userType'},
                {data: 'creationDate', name: 'created'},
                {data: 'updateDate', name: 'updated'},
//                    {defaultContent: '', orderable: false},
                {data: null, orderable: false, searchable: false, render: function (info, type, row) {
                        return '<a href="#" onclick="$.Manager.pages.Users.onEditUser(' + info.id + ')">edit user</a>';
                    }
                }
            ]
        };
     
        if (! $.Manager.isPrototype) {
            $.extend(dtConfig, {
                ajax: function(data, callback, settings) {
                    $.rest.GET('/user/list' + createListQuery(data),
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
        
        this.$table = this.$page.find('#usersTable').DataTable(dtConfig);

        this.initSearchBoxValues({
            username: this.$table.column('username:name').search(),
            userType: this.$table.column('userType:name').search()
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
//        $page.find("select").select2();
    },
    
    initSearchBoxValues: function(searchValues) {
        var $searchBox = this.$page.find('#searchBox');
        
        Object.keys(searchValues).forEach(function(name) {
            $searchBox.find('#' + name).val(searchValues[name]);
        });
        
        $searchBox.find('select').trigger("change");
    },
    
    getSearchBoxValues: function() {
        var values = {};
    
        var $searchBoxControls = this.$page.find('#searchBox .form-control');
        $searchBoxControls.each(function(index, $control) {
            values[$control.id] = $control.value.trim();
        }); 
        
        return values;
    },

    filterTable: function() {
        const self = this;
        const searchValues = self.getSearchBoxValues()
        
        console.info('Searching for: ', searchValues);
        
        Object.keys(searchValues).forEach(function(name) {
            self.$table.column(name + ':name').search(searchValues[name]);
        });

        this.$table.draw();
    },
    
    onEditUser: function(userId) {

        const self = this;
        self.setDataToEditUserModal({});

        self.showEditUserModal();
        
        if ($.Manager.isPrototype) {
            self.setDataToEditUserModal({
                id: userId,
                username: 'teamowner_tester',
                userType: 'TEAM_OWNER'
            });
            return;
        }
        
        $.rest.GET(
            '/user/' + userId,
            function(respData) {
                if (respData != undefined) {
//                    console.debug(respData);
                    self.setDataToEditUserModal(respData);
                    self.editUserId = respData.id;
                }
            }
        );
    },
    
    setDataToEditUserModal: function(data) {
        this.find$editUserModal().find('form').deserializeObject(data);
    },

    getDataFromEditUserModal: function() {
        return this.find$editUserModal().find('form').serializeObject();
    },
    
    showEditUserModal: function() {
        this.find$editUserModal().modal('show');
    },
    
    hideEditUserModal: function() {
        this.find$editUserModal().modal('hide');
        this.editUserId = null;  
    },
    
    find$editUserModal: function() {
        return this.$page.find('#editUserModal');
    },
    
    resetFilter: function() {
        var $searchBox = this.$page.find('#searchBox');
        $searchBox.find('.form-control').val('');
        $searchBox.find('select').trigger("change");
        this.filterTable();
    },
    
    saveUser: function () {
        
        const self = this;
        const data = self.getDataFromEditUserModal();
        
        console.info('updating user (id=' + self.editUserId + '): ', data);
        
        if ($.Manager.isPrototype) {
            setTimeout(
                function() {
                    self.hideEditUserModal();    
                },
                2000);
            return;
        }
        
        $.rest.PUT('/user/' + self.editUserId, data,
            function(respData) {
//                console.debug(respData);
                self.hideEditUserModal();
                self.$table.draw();
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
        $.Manager.pages.Users.onLoad($body, null);
        $.Manager.pages.Users.onShow($body, null);
    } else {
        $.Manager.pages.setHandlers(
            function($page, params) {
                $.Manager.pages.Users.onLoad($page, params);
            },
            function($page, params) {
                $.Manager.pages.Users.onShow($page, params);
            });
    } 
});