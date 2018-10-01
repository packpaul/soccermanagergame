/**
 * Расширения для плагина inputmask, специфичные для приложения.
 */

(function ($) {
    $.extend($.inputmask.defaults.aliases, {
        'currency': {
            alias: 'decimal',
            allowPlus: false,
            allowMinus: false,
            autoGroup: true,
            groupSeparator: ' ',
            autoUnmask: true,
            onUnMask: function(m, u) {
                return (u) ? u.replace(/ /g, '') : '';
            }
        },
        'age': {
            alias: 'decimal',
            allowPlus: false,
            allowMinus: false,
            digits: 2
         },
        'date': {
            alias: 'yyyy-mm-dd',
            placeholder: 'yyyy-mm-dd'
         }
    });
})(jQuery);