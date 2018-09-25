if (typeof jQuery === "undefined") {
    throw new Error("jQuery is required!");
}

/**
 * Returns parameter value from URL
 */
$.urlParam = function(name) {
    var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
    return (results != null) ? results[1] || 0 : null;
}

/**
 * Serializes jQuery-object of a form to Object.
 * Usage example: var theFormJSON = $('form #theForm').serializeObject();
 * Taken from http://jsfiddle.net/sxGtM/3/
 */
$.fn.serializeObject = function()
{
    var o = {};
    var a = this.serializeArray();
    $.each(a, function() {
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    
    return o;
};

/**
 * Decerializes Object to jQuery-object of a form.
 * Usage example: $('form #theForm').deserializeObject({ name : 'Peteson'});
 */
$.fn.deserializeObject = function(o) {
    $.each(this.find('input[name]'), function() {
        var $this = $(this);
        const value = o[$this.attr('name')];
        switch ($this.attr('type')) {
            case 'radio': {
                $this.prop('checked', ($this.val() == value));
                break;
            }
            default: {
                $this.val(value ? value : '');
            }
        }
    });
    $.each(this.find('select[name]'), function() {
        var $this = $(this);
        const value = o[$this.attr('name')];
        $this.val(value || '');
    });
}

/**
 * Makes elements on a form unaccessable for input
 */
$.fn.disableInput = function() {
    var $this = $(this);
    if ($this.prop('tagName') === 'FORM') {
        $this.find('[name]').prop('disabled', true);
    }
}

/**
 * Makes elements on a form accessable for input
 */
$.fn.enableInput = function() {
    var $this = $(this);
    if ($this.prop('tagName') === 'FORM') {
        $this.find('[name]').prop('disabled', false);
    }
};