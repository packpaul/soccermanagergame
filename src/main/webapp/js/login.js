/**
 * Model and logic of /login.html
 */

function doLogin() {
    
    function hintIncorrectUser() {
        $('#errorTooltip').text('Incorrect user or password').parent().removeClass('hidden');
        $('#username').parent().addClass('has-error');
        $('#password').parent().addClass('has-error');
    }
    function hintUserExists() {
        $('#errorTooltip').text('User with the same username exists already').parent().removeClass('hidden');
        $('#username').parent().addClass('has-error');
    }

    function hintUsernameEmpty() {
        $('#errorTooltip').text('Username should be not empty').parent().removeClass('hidden');
        $('#username').parent().addClass('has-error');
    }
    function hintPasswordEmpty() {
        $('#errorTooltip').text('Password should be not empty').parent().removeClass('hidden');
        $('#password').parent().addClass('has-error');
    }
    function hintPasswordDiffer() {
        $('#errorTooltip').text('Passwords should coinside').parent().removeClass('hidden');
        $('#password').parent().addClass('has-error');
        $('#repeatPassword').parent().addClass('has-error');
    }

    function unhint() {
        $('._inputs .form-group').removeClass('has-error');
        $('#errorTooltip').parent().addClass('hidden');
    }
    
    unhint();
    
    if (isLoginAsNewUser()) {
        if ($('#username').val() == '') {
            hintUsernameEmpty();
            return
        }
        if ($('#password').val() == '') {
            hintPasswordEmpty();
            return
        }
        if ($('#password').val() != $('#repeatPassword').val()) {
            hintPasswordDiffer();
            return;
        }
        $.rest.signup($('#username').val(), $('#password').val(),
            function() {
                unhint();
                window.location.replace('/manager');
            },
            function(status, respData) {
                hintUserExists();
            }
        );
    } else {
        $.rest.login($('#username').val(), $('#password').val(),
            function() {
                unhint();
                window.location.replace('/');
//                window.location.reload(true);
            },
            function(status, respData) {
                hintIncorrectUser();
            }
        );
    }

}


function onLoginAsNewUser() {
    var $group = $('#repeatPassword').parent(); 
    if (isLoginAsNewUser()) {
        $('#repeatPassword').val('');
        $group.removeClass('hidden');
    } else {
        $group.addClass('hidden');
    }
}


function isLoginAsNewUser() {
    var isChecked = $('#loginAsNewUser').prop('checked');
    
    return isChecked;
}
