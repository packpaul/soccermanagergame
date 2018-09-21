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

$.Manager.pages._Sample = {
    onShowing: function($page, params) {
        console.log('_Sample page is showing.');
    },
    
    onLoad: function($page, params) {
        console.log('_Sample page is loaded.');
    }
};

$(function() {
    if ($.Manager.isPrototype) {
        var $body = $(document.body);
        $.Manager.pages._Sample.onShowing($body, null);
        $.Manager.pages._Sample.onLoad($body, null);
    } else {
        $.Manager.pages.setHandlers(
            function($page, params) {
                $.Manager.pages._Sample.onShowing($page, params);
            },
            function($page, params) {
                $.Manager.pages._Sample.onLoad($page, params);
            });
    } 
 });