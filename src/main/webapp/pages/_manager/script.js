'use strict';

$.Manager = {};

$.Manager.doLoadPage = function(page) {
    // TODO:
}

$.Manager.doLogout = function() {
    
    if (! confirm('Do you really want to log-out?')) {
        return;
    }
    
    var callback = function() {
        window.location.replace('/login.html');  
    };
    
    $.rest.logout(callback, callback);
}