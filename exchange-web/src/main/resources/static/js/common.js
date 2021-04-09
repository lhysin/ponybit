jQuery.fn.serializeObject = function() {
    var obj = null;
    try {
        if (this[0].tagName && this[0].tagName.toUpperCase() == "FORM") {
            var arr = this.serializeArray();
            if (arr) {
                obj = {};
                jQuery.each(arr, function() {
                    obj[this.name] = this.value;
                });
            }
        }
    } catch (e) {
        alert(e.message);
    } finally {
    }
 
    return obj;
};


var common = {};

common.serverError = function(result, isAlert){

//    var alertMsg = 'A Server Error Occured.\nPleas contact your server administrator.';
    var alertMsg = '서버오류가 발생하였습니다.\n서버 관리자에게 연락부탁드립니다.';

    if(!result){
        alert(alertMsg);
        return;
    } else {
        // auto logout (session time limit)
        if(result.status === 401){
            common.showLoadingBar(true, true);
            location.href = '/login';
            return;
        }
    }

    // check business msg
    if(!!result.cause && !!result.cause.code){
        alertMsg = result.cause.msg;
    }

    if(isAlert){
        alert(alertMsg);
    }
}

common.numberWithCommas = function(str) {
    var parts = str.toString().split(".");
    return parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",") + (parts[1] ? "." + parts[1] : "");
}

common.showLoadingBar = function(isFull, isDimmed){

    var isExist = $('body.spinner').length > 0;

    var spinner = '<div class="spinner"><i class="fa fa-spinner fa-spin" style="font-size:40px"></i></div>';
    if(!isExist){
        $('html').css('height', '100%');
        $('html').css('overflow', 'hidden');
        $('body').prepend(spinner);
    } else {
        return false;
    }

    var $spinner = $('.spinner');

    if(isFull){
        $spinner.addClass('fixed');
    } else {
        $spinner.removeClass('fixed');
    }
    if(isDimmed){
        $spinner.addClass('dimmed');
    } else {
        $spinner.removeClass('dimmed');
    }
}

common.hideLoadingBar = function(){
    var isExist = $('body .spinner').length > 0;
    if(!!isExist){
        $('html').css('height', '');
        $('html').css('overflow', '');
        $('.spinner').remove();
    }
}

common.firstUpperCase = function(value) {
    if(!!value && !!value.toLowerCase){
        value = value.toLowerCase();
        value = value.charAt(0).toUpperCase() + value.substr(1);
        return value;
    }
}

$.ajaxSetup({
    beforeSend : function(xhr) {
    }
});

// https://naver.com
// protocol + host + port
common.getCurrentDomain = function(){
    var protocol = location.protocol;
    var hostname = window.location.hostname;
    var port = window.location.port;
    var domain = protocol + '//' + hostname;

    if(!!port){
        domain += (':' + port);
    }

    return domain;
}

common.getQueryParam = function(){
    var queries = {};
    $.each(document.location.search.substr(1).split('&'),function(c,q){
        var i = q.split('=');
        if(!!i[0] && !!i[1]){
            queries[i[0].toString()] = i[1].toString();
        }
    });
    return queries;
}

/*
 * navigation bar
 */
$(document).ready(function(){
    $(document).on("click", ".cursor-default[href='#']", function(){
        return false; 
    });
    $('[data-toggle="tooltip"]').tooltip();
});

/*
 * form validation
 */
$(document).ready(function(){
    var forms = document.getElementsByClassName('needs-validation');
    var validation = Array.prototype.filter.call(forms, function(form) {
        form.addEventListener('submit', function(event) {
            form.classList.add('was-validated');
            if (form.checkValidity() === false) {
                event.preventDefault();
                event.stopPropagation();
            }
        }, false);
    });
});
