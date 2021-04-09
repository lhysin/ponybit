var isOnLoad = false;
var active = {};
/**
 * screen init
 */
$(document).ready(function(){

    var hash = location.hash;
    if(!!hash && hash.length > 1){
        hash = hash.substring(1);
    }

    common.showLoadingBar(true, false);

    if(!hash){
        isOnLoad = true;
        common.hideLoadingBar();
        active.renderDefaultHtml();
        return;
    }

    hash = encodeURIComponent(hash);

    $.ajax({
        type: 'put',
        url: '/api/v1.0/users/' + hash + '/active'
    }).done(function(result) {
//        alert('Success account active!\nTry Login Again');
        alert('계정이 활성화 되었습니다.\n다시 로그인해 주세요.');
        location.href = '/login';
    }).fail(function(xhr, status, error) {

        var result = xhr.responseJSON;
        common.serverError(result, false);

        if(!!result.cause){
            var code = result.cause.code;
            // token is invalid, user not exsit
            if(code === 2010 || code === 2002){
                alert('잘못된 요청입니다.\n이메일 인증을 다시 시도해주세요.');
                location.href = '/active';
                // user already active
            } else if(code === 2003){
                alert('이미 활성화된 계정입니다.\n로그인페이지로 이동합니다.');
                location.href = '/login';
                // token is expired
            } else if(code === 2011){
                alert('이메일 인증 시간이 초과되었습니다.\n이메일 인증을 재시도 하세요.');
                active.renderDefaultHtml();
                common.hideLoadingBar();
            }
        }

    }).always(function(){
        isOnLoad = true;
    });
});

$(document).ready(function(){
    $(document).on('submit', '#activeForm', function(event){

        event.preventDefault();

        if(!isOnLoad){
            return false;
        }

        common.showLoadingBar(true, true);

        var data = $(this).serializeObject();
//        var email = encodeURIComponent(btoa(data.email))
        var email = encodeURIComponent(data.email);
        $.ajax({
            type: 'put',
            url: '/api/v1.0/users/' + email + '/resend/'
        }).then(function(data) {
//            alert('Send Mail Success.\nCheck your email!');
            alert('이메일의 메일함을 확인해주세요.');
            location.href = '/login';
        }).fail(function(xhr, status, error) {

            var result = xhr.responseJSON;
            common.serverError(result, false);

            if(!!result.cause){
                var code = result.cause.code;
                // user not exsit
                if(code === 2002){
                    alert('존재하지 않는 이메일주소입니다.');
                // user already active
                } else if (code === 2003){
                    alert('이미 활성화된 계정입니다.\n로그인페이지로 이동합니다.');
                    location.href = '/login';
                // too many request
                } else if (code === 0003){
					var cause = result.cause;
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

active.renderDefaultHtml = function(){

    var html = '';
    html += '      <form class="needs-validation" id="activeForm" novalidate>';
    html += '          <h1 class="h1 mb-3 font-weight-normal text-center">계정 활성화</h1>';
    html += '          <div class="form-group row">';
    html += '            <label for="email" class="col-sm-3 col-form-label">이메일 주소</label>';
    html += '            <div class="col-sm-9">';
    html += '                <input type="email" class="form-control" name="email" placeholder="이메일 주소" required="required">';
    html += '                <div class="invalid-feedback">이메일 주소를 입력해 주세요.</div>';
    html += '            </div>';
    html += '          </div>';
    html += '          <div class="form-group">';
    html += '              <button type="submit" class="btn btn-pony btn-block btn-lg" id="btnVerify">계정 활성화</button>';
    html += '          </div>';
    html += '          <p>이메일을 입력후 계정활성화를 진행해주세요.</p>';
    html += '      </form>';

    $('#activeContainer').html(html);

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
}