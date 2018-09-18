/**
 * Model and logic of /login.html
 */

function doLogin() {
    
    function hintIncorrectUser() {
        $('form>.form-group').addClass('has-error');
        $('#errorTooltip').removeClass('hidden');
    }
    function unhintIncorrectUser() {
        $('form>.form-group').removeClass('has-error');
        $('#errorTooltip').addClass('hidden');
    }
    
    unhintIncorrectUser();
    
    $.rest.login($('#login').val(), $('#password').val(),
        function() {
            unhintIncorrectUser();
            window.location.replace('/manager');
//            window.location.reload(true);
        },
        function(status, respData) {
            hintIncorrectUser();
        }
    );
}