$(document).ready(function(){
    $('#loginForm').on('submit', function(event) {

        var formObj = document.getElementById('loginForm');
        if (!formObj.checkValidity()) {
            return false;
        }

        event.preventDefault();
        common.showLoadingBar(true, true);

        var data = $(this).serialize();
        $.ajax({
            type: 'post',
            url: '/login-check',
            data: data
        }).done(function(result) {
            // login success
            var queryStrMap = new URLSearchParams(location.search);
            if(queryStrMap.has('returnUrl')){
                location.href = queryStrMap.get('returnUrl');
            } else {
                location.href = '/';
            }
        }).fail(function(xhr, status, error) {

            var result = xhr.responseJSON;
            common.serverError(result, false);

            if(!!result.cause){
                var code = result.cause.code;
                if(code === 2004){
                    alert('활성화 되지 않은 계정입니다.');
                    location.href = '/active';
                    return false;
                }
            }
            alert('로그인에 실패하였습니다.');

        }).always(function(){
            common.hideLoadingBar();
        });
    });

});