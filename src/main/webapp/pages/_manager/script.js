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
    }

};


(function() {

    var pageHandlers = {};
    var currentPage;
    
    // "page" loader
    function app(pageName, param) {
    
        var $page = $(document.body).find("section.content#" + pageName);  
      
        var src = $page.attr("src");
        if (src && ($page.find(">:first-child").length == 0)) {
            // it has src and is empty - load it
            $.get(src, "html")
                .done(function(html) {
                    currentPage = pageName;
                    $page.html(html);
                    show(pageName, param);
                 })
                .fail(function() {
                    $page.html("failed to get " + src);
                    show(pageName, param);
                 });
        } else {
            show(pageName, param);
        }
    }
    
    // show the "page" with optional parameter
    function show(pageName, param) { 
        // invoke page handler
        var ph = pageHandlers[pageName]; 
        if (ph) { 
            var $page = $("section.content#" + pageName);
            ph.call($page.length ? $page[0] : null, param); // call "page" handler
        }

        // activate the page  
        $("ul.sidebar-menu").find("li").removeClass("active");
        $("ul.sidebar-menu").find("a[href='#" + pageName + "']").closest("li").addClass("active");
        // TODO: enable upper level in tree menus
      
        $(document.body).attr("page", pageName)
                        .find("section.content").addClass("hidden")
                        .filter("section.content#" + pageName).removeClass("hidden");
    }

    // register page handler  
    app.handler = function(handler) {
        var $page = $(document.body).find("section.content#" + currentPage);  
        pageHandlers[currentPage] = handler.call($page[0]);
    }
    
    function onhashchange() {
        var hash;
        var param;
        
        if (location.hash) {
            var rexp = /#([-_0-9A-Za-z]+)(\:(.+))?/;
            var match = rexp.exec(location.hash);
            hash = match[1];
            param = match[3];
        } else {
            hash = $("section.content")[0].id;
        }

        app(hash, param); // navigate to the page
    }
    
    window.onhashchange = onhashchange; // attach hashchange handler
    
    $.Manager.app = app; // setup the app as global object
    
    $(function() {
        // initial state setup
        window.onhashchange();
     });

  })();