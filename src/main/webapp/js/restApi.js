if (typeof jQuery === "undefined") {
    throw new Error("jQuery is required!");
}

$.rest = {};
$.html = {};

/**
 * GET
 */
$.rest.GET = function(path, callback, failCallback) {
    _restAjax("/manager/api", path, "GET", callback, null, failCallback);
}

/**
 * POST
 */
$.rest.POST = function(path, reqData, callback, failCallback) {
    _restAjax("/manager/api", path, "POST", callback, reqData, failCallback);
}

/**
 * PUT
 */
$.rest.PUT = function(path, reqData, callback, failCallback) {
    _restAjax("/manager/api", path, "PUT", callback, reqData, failCallback);
}

/**
 * DELETE
 */
$.rest.DELETE = function(path, callback, failCallback) {
    _restAjax("/manager/api", path, "DELETE", callback, failCallback);
}

/**
 * GET HTML
 */
$.html.GET = function(url, callback, failCallback) {
    _ajax(url, "", "GET", callback, null, failCallback, "html");
}

function _restAjax(apiRoot, path, reqType, callback, reqData, failCallback) {

    if (reqData) {
        reqData = JSON.stringify(reqData);
    }
    
    _ajax(apiRoot, path, reqType, callback, reqData, failCallback, "json", "application/json");
}

function _ajax(apiRoot, path, reqType, callback, reqData, failCallback, dataType, contentType) {
    
    function ajaxCall(failCallback) {
        const uri = getUri();

        $.ajax({
            url: uri,
            data: reqData,
            type: reqType,
            contentType: contentType,
            dataType: dataType
        }).done(function (respData) {
            if (callback) {
                callback(respData);
            }
        }).fail(function(resp) {
            _restOnError(uri, resp.status, resp.responseJSON);
            if (failCallback) {
                failCallback(resp.status, resp.responseJSON);
            }
        });
    }
    
    function getUri() {
        let s = path.indexOf("?");
        let method = (s >= 0) ?  path.substring(0, s) : path; 
        return apiRoot + method + $.query.load(path).set("access_token", Cookies.get("access_token"));
    }
    
    ajaxCall(function(status, respData) {
        if ((status == 401) && respData && (respData.error == "invalid_token")) {
            $.rest.login.refresh(function() {
                ajaxCall(failCallback);
            },
            function() {
                window.location.replace('/');
            });
        } else {
            if (failCallback) {
                failCallback(status, respData);
            }
        }
    });
}

function _restOnError(uri, status, data) {
    var dataError = (data) ? data.error : undefined;
    console.error(">> " + uri + " - " + status + ": " + dataError);
    if (status >= 500) {
        alert("Server connection error!");
    }
}

$.rest.login = function(username, password, callback, failCallback) {

    const uri = "/auth/api/login" + $.query.empty().set("grant_type", "password")
                                                   .set("client_id", "managerClient")
                                                   .set("username", username)
                                                   .set("password", password).toString();
    $.ajax({
        url: uri,
        type: "POST",
        contentType: "application/json",
        dataType: "json"
    }).done(function (respData) {
        Cookies.set("access_token", respData.access_token);
        Cookies.set("refresh_token", respData.refresh_token, { expires: 30 });
        if (callback) {
            callback();
        }
    }).fail(function(resp) {
        Cookies.remove("access_token");
        _restOnError(uri, resp.status, resp.responseJSON);
        if (failCallback) {
            failCallback(resp.status, resp.responseJSON);
        }
    });
}

$.rest.signup = function(username, password, callback, failCallback) {

    const uri = "/auth/api/login" + $.query.empty().set("grant_type", "signup")
                                                   .set("client_id", "managerClient")
                                                   .set("username", username)
                                                   .set("password", password).toString();
    $.ajax({
        url: uri,
        type: "POST",
        contentType: "application/json",
        dataType: "json"
    }).done(function (respData) {
        Cookies.set("access_token", respData.access_token);
        Cookies.set("refresh_token", respData.refresh_token, { expires: 30 });
        if (callback) {
            callback();
        }
    }).fail(function(resp) {
        Cookies.remove("access_token");
        _restOnError(uri, resp.status, resp.responseJSON);
        if (failCallback) {
            failCallback(resp.status, resp.responseJSON);
        }
    });
}

$.rest.login.refresh = function(callback, failCallback) {
    
    const uri = "/auth/api/login" + $.query.empty().set("grant_type", "refresh_token")
                                                   .set("client_id", "managerClient")
                                                   .set("refresh_token", Cookies.get("refresh_token"));
    
    $.ajax({
        url: uri,
        type: "POST",
        contentType: "application/json",
        dataType: "json"
    }).done(function (respData) {
        Cookies.set("access_token", respData.access_token);
        if (callback) {
            callback(respData.access_token);
        }
    }).fail(function(resp) {
        Cookies.remove("access_token");
        Cookies.remove("refresh_token");
        _restOnError(uri, resp.status, resp.responseJSON);
        if (failCallback) {
            failCallback(resp.status, resp.responseJSON);
        }
    });
}

$.rest.logout = function(callback, failCallback) {
    
    _restAjax("/auth/api", "/logout", "POST",
            function() {
                Cookies.remove("access_token");
                Cookies.remove("refresh_token");
                if (callback) {
                    callback();
                }
            },
            undefined,
            failCallback);
}
