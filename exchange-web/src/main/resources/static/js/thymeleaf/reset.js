var resetPage = {};

$(document).ready(function(){
    $('#resetForm').on("submit", function(event) {

        var formObj = document.getElementById('resetForm');
        if (!formObj.checkValidity()) {
            return false;
        }

        event.preventDefault();
        common.showLoadingBar(true, true);

        var data = $(this).serializeObject();
//        var email = encodeURIComponent(btoa(data.email))
        var email = encodeURIComponent(data.email);
        $.ajax({
            type: 'put',
            url: '/api/v1.0/users/' + email + '/reset/'
        }).done(function(data) {
            //alert('Password reset Success.\nCheck your email!');
            //location.href = '/login';
            resetPage.alertWarn();
        }).fail(function(xhr, status, error) {

            var result = xhr.responseJSON;
            common.serverError(result, false);

            if(!!result.cause){
                var cause = result.cause;
                // user not exsit
                if(cause.code === 2002){
                    resetPage.alertWarn();
                // too many request
                } else if (cause.code === 0003){
                    if(!!cause.data && cause.data.leftTimeSec){
                        alert('요청횟수가 많습니다.' + '\n' + cause.data.leftTimeSec + '초 후 다시시도해 주세요.');
                    }
                }
            }
        }).always(function(){
            common.hideLoadingBar();
        });
    });
});


resetPage.alertWarn = function(){
    var alertWarn = '<div class="alert alert-warning">입력하신 이메일의 받은 편지함에서 다음 단계를 확인하세요.<br>안내 내용을 받지 못하고 스팸 폴더에 없는 경우<br>다른 주소로 가입했음을 의미 할 수 있습니다.</div>';

    var $form = $('#resetForm');
    $form.find('p').nextAll().remove();
    $form.append(alertWarn);
    $form.html(alertWarn);
}