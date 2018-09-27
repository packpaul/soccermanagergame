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

    onMessageUser: function(receiver) {
        alert('// TODO:');
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
     });

  })();