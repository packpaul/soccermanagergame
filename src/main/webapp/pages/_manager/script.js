'use strict';

$.Manager = {
        
    doLogout: function() {
        if (! confirm('Do you really want to log-out?')) {
            return;
        }
        
        var callback = function() {
            window.location.replace('/login.html');  
        };
        
        $.rest.logout(callback, callback);
    },

    onMessageUser: function(toUsername) {
        var message = prompt("Message to user '" + toUsername + "':", "Hi! ...");
        if (! message) {
            return;
        }
        
        $.rest.POST(
            '/message',
            {
                toUsername: toUsername,
                content: message
            },
            undefined,
            function(status, respData) {
                alert(respData.error_description);
            }
        );
    },
    
    receiveMessages: function() {
        $.rest.GET(
            '/message',
            function(respData) {
                respData.forEach(function(message) {
                    alert("Message from user '" + message.fromUsername + "'\n" +
                          "Date: " + message.creationDate + "\n\n" +
                          message.content);
                    $.rest.POST(
                        '/message/' + message.id + '/read',
                        null,
                        undefined,
                        function(status, respData) {
                            alert(respData.error_description);
                        }
                    );
                });
            },
            function(status, reqData) {
            }
        );
    },
    
    select2: {
        _config: {
            users: {
                idUrl: "/user/",
                listUrl: "/user/list",
                filterProperty: "username",
                renderCallback: function(user) {
                    return user.id + ' - ' + user.username;
                }
            },
            players: {
                idUrl: "/player/",
                listUrl: "/player/list",
                filterProperty: "fullName",
                renderCallback: function(player) {
                    return player.id + ' - ' + player.fullName + ', ' + player.country;
                }
            },
            teams: {
                idUrl: "/team/",
                listUrl: "/team/list",
                filterProperty: "teamName",
                renderCallback: function(team) {
                    return team.id + ' - ' + team.teamName + ', ' + team.country;
                }
            }
        },

        users: function($select) {
            $select.prop('select2type', 'users');
            this._create($select);
        },

        players: function($select) {
            $select.prop('select2type', 'players');
            this._create($select);
        },

        teams: function($select) {
            $select.prop('select2type', 'teams');
            this._create($select);
        },

        select: function($select, id) {
            const select2type = $select.prop('select2type');
            const url = this._config[select2type].idUrl;
            const renderCallback = this._config[select2type].renderCallback;            

            $.rest.GET(url + id,
                    function(respData) {
                    if (respData != undefined) {
                        const optionSel = "option[value='" + respData.id + "']"; 
                        $select.children(optionSel).remove();
                        $select.append(new Option(renderCallback(respData), respData.id, true, true));
                        $select.trigger('change');
                    }
                }
            );
        },

        _create: function($select) {
            const select2type = $select.prop('select2type');
            const url = this._config[select2type].listUrl;
            const filterProperty = this._config[select2type].filterProperty;
            const renderCallback = this._config[select2type].renderCallback;
            
            $select.select2({
                ajax: {
                    url: url,
                    transport: function (params, success, failure) {
                        $.rest.GET(params.url + params.data, success, failure);
                    },
                    data: function (params) {
                        const pageSize = 30;
                        var query = $.query.empty();
                        query = query
                             .set('filterProperties', params.term ? [filterProperty] : [])
                             .set('filterValues', params.term ? ['*' + params.term + '*'] : [])
                             .set('orderProperty', 'id')
                             .set('orderDir', 'asc')
                             .set('offset', params.page ? pageSize * (params.page - 1) : 0)
                             .set('limit', pageSize)
                             
                        return query.toString();
                    },

                    processResults: function (data, params) {
                        params.page = params.page || 1;

                        return {
                            results: data.data,
                            pagination: {
                                more: (30 * params.page < data.countFiltered)
                            }
                        };
                    },
                    cache: true
                },
                placeholder: '-- no selection --',
//                minimumInputLength: 2,
                delay: 500,
                templateResult: function(data) {
                    return renderCallback(data);
                },
                escapeMarkup: function(markup) {
                    return markup;
                },
                templateSelection: function(data) {
                    if (data.id) {
                        return data.text ? data.text : renderCallback(data);
                    }
                    return data.text;
                }
            });
        }
    }

};


(function() {

    var currentPage;
    
    var onShowPageHandlers = {};
    var onLoadPageHandlers = {};

    // "page" loader
    function app(pageName, param) {

        var $page = find$page(pageName);  
        var src = $page.attr("src");

        if (src && ($page.find(">:first-child").length == 0)) {
            // it has src and is empty - load it
            $.html.GET(src,
                function(html) {
                    currentPage = pageName;
                    load(pageName, html, param);
                    show(pageName, param);
                },
                function() {
                    $page.html("failed to get " + src);
                    show(pageName, param);
                });
        } else {
            show(pageName, param);
        }
    }
    
    function find$page(pageName) {
        return $(document.body).find("section.content#" + pageName + ":first");
    }
    
    function load(pageName, html, param) {
        var $page = find$page(pageName);
        $page.addClass('page-' + pageName);
        $page.html(html);
        var handler = onLoadPageHandlers[pageName]; 
        if (handler) { 
            handler($page.length ? $page : null, param);
        }
    }
    
    // show the "page" with optional parameter
    function show(pageName, param) {
        var $page = find$page(pageName); 
        
        // activate the page  
        $("ul.sidebar-menu").find("li").removeClass("active");
        $("ul.sidebar-menu").find("a[href='#" + pageName + "']").closest("li").addClass("active");
        // TODO: enable upper level in tree menus
        
        var name = $page.attr("pageName")
        $("section.content-header > h1").text((name) ? name : "");

        $(document.body).find("section.content").addClass("hidden");
        $page.removeClass("hidden");
        
        var handler = onShowPageHandlers[pageName]; 
        if (handler) { 
            handler($page.length ? $page : null, param);
        }
    }

    // register page handler  
    app.setHandlers = function(onLoadPageHandler, onShowPageHandler) {
        var $page = find$page(currentPage);
        if (onLoadPageHandlers) {
            onLoadPageHandlers[currentPage] = onLoadPageHandler;
        }
        if (onShowPageHandler) {
            onShowPageHandlers[currentPage] = onShowPageHandler;
        }
    }
    
    function onhashchange(hash) {
        
        if (! hash) {
            return;
        }
        
        var rexp = /#([-_0-9A-Za-z]+)(\:(.+))?/;
        var match = rexp.exec(hash);
        var page = match[1];
        var param = match[3];

        // navigate to the page
        app(page, param);
    }
    
    window.onhashchange = function() {
        onhashchange(location.hash);
    }
    
    $.Manager.pages = app;
    
    $(function() {
        // initial page setup
        onhashchange(location.hash || ('#' + $("section.content")[0].id));
        $.Manager.receiveMessages();
     });

  })();
