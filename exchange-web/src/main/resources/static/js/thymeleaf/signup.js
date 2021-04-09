
$(document).ready(function(){

    var params = common.getQueryParam();
    if(!!params.r){
        $('#refCd').val(params.r);
    }

    $('#signUpForm').on('submit', function(event) {

        var formObj = document.getElementById('signUpForm');
        if (!formObj.checkValidity()) {
            return false;
        }

        event.preventDefault();
        common.showLoadingBar(true, true);

        var data = $(this).serializeObject();
        $.ajax({
            type: 'post',
            url: '/api/v1.0/users',
            contentType: 'application/json',
            data: JSON.stringify(data)
        }).done(function(result) {
//            alert('signup is successful\ncheck your email.');
            alert('회원가입이 완료되었습니다.\n이메일의 메일함을 확인해주세요.');
            location.href = '/login'
        }).fail(function(xhr, status, error) {

            var result = xhr.responseJSON;
            common.serverError(result, false);

            if(!!result.cause){
                var code = result.cause.code;
                // user already exist
                if(code === 2001){
                    alert('이미 회원가입된 이메일 주소입니다.');
                } else {
                    alert('회원가입에 실패하였습니다.');
                }
            }
        }).always(function(){
            common.hideLoadingBar();
        });

        return false;
    });

    var matchPassword = function(){
        var confirmPassword = document.getElementById('confirmPassword');
        if($('#password').val() != $('#confirmPassword').val()) {
            confirmPassword.setCustomValidity('패스워드가 동일하지 않습니다.');
            confirmPassword
        } else {
            confirmPassword.setCustomValidity('');
        }

    }

    $('#password').keyup(function(){
        matchPassword();
    });

    $('#confirmPassword').keyup(function(){
        matchPassword();
    });

});

