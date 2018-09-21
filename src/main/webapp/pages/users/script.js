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
    onShowing: function($page, params) {
        console.log('Users page is showing.');
    },
    
    onLoad: function($page, params) {
        console.log('Users page is loaded.');
    }
};

$(function() {
    if ($.Manager.isPrototype) {
        var $body = $(document.body);
        $.Manager.pages.Users.onShowing($body, null);
        $.Manager.pages.Users.onLoad($body, null);
    } else {
        $.Manager.pages.setHandlers(
            function($page, params) {
                $.Manager.pages.Users.onShowing($page, params);
            },
            function($page, params) {
                $.Manager.pages.Users.onLoad($page, params);
            });
    } 
 });