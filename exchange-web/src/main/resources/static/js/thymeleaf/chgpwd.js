var chgPwd = {};

$(document).ready(function(){

    var loginUser = chgPwd.loginUser;
    if(!!loginUser){

        var refCnt = loginUser.refCnt;
        if(!refCnt || refCnt < 1){
            refCnt = 0;
        }

        var $modalHtml = '';
        $modalHtml += '  <div class="modal fade" id="refModal">';
        $modalHtml += '    <div class="modal-dialog modal-dialog-centered">';
        $modalHtml += '      <div class="modal-content">';
        $modalHtml += '        <div class="modal-header">';
        $modalHtml += '          <h4>추천현황</h4>';
        $modalHtml += '          <button type="button" class="close" data-dismiss="modal">&times;</button>';
        $modalHtml += '        </div>';
        $modalHtml += '        <div class="modal-body">';
        $modalHtml += '            <div class="container info-modal">';
        $modalHtml += '                <h6 class="info-modal-text1" id="refCdHead">' + loginUser.email + '님의<br>추천인코드는 ' + loginUser.myRefCd + '입니다.</h6>';
        $modalHtml += '                <h6 class="info-modal-text2" id="refUrlHead">' + common.getCurrentDomain() + '/signup?r=' + loginUser.myRefCd + '</h6>';
        $modalHtml += '                <button class="btn info-modal-btn" id="btnRefCopy" type="button">복사하기</button>';
        $modalHtml += '                <h6 class="info-modal-text3">나를 추천한 회원수</h6>';
        $modalHtml += '                <h6 class="info-modal-text4" id="refCnt">' + refCnt + '명</h6>';
        $modalHtml += '            </div>';
        $modalHtml += '        </div>';
        $modalHtml += '        <div class="modal-footer">';
        $modalHtml += '            <div class="row">';
        $modalHtml += '                <div class="container">';
        if(refCnt > 0){
            $modalHtml += '                <div class="alert alert-success small">카카오톡 인증 후 추천으로 지급된 포니코인을 사전예약 페이지에서 확인해보세요!</div>';
        }
        $modalHtml += '                </div>';
        $modalHtml += '                <div class="container">';
        $modalHtml += '                    <button type="button" class="btn btn-secondary float-right" data-dismiss="modal">닫기</button>';
        $modalHtml += '                </div>';
        $modalHtml += '            </div>';
        $modalHtml += '        </div>';
        $modalHtml += '      </div>';
        $modalHtml += '    </div>';
        $modalHtml += '  </div>';
        $('body').append($modalHtml);
    }

    $('#openRefModal').click(function(){
        event.preventDefault();
        var loginUser = chgPwd.loginUser;
        if(!loginUser){
            alert('서버오류가 발생하였습니다.\n서버 관리자에게 연락부탁드립니다.');
            return false;
        }
        $('#refModal').modal();
    });

    // referral url copy
    $(document).on('click', '#btnRefCopy', function(){
        var $temp = $('<input>');
        $('body').append($temp);
        $temp.val($('#refUrlHead').text()).select();
        document.execCommand('copy');
        $temp.remove();
        alert('복사되었습니다.');
    });

    $('#chgPwdForm').on('submit', function(event) {

        var formObj = document.getElementById('chgPwdForm');
        if (!formObj.checkValidity()) {
            return false;
        }

        if($('#password').val() === $('#newPassword').val()) {
            alert('동일한 패스워드로 변경하실 수 없습니다.');
            return false;
        }

        event.preventDefault();
        common.showLoadingBar(true, true);

        var data = $(this).serializeObject();
        $.ajax({
            type: 'put',
            url: '/api/v1.0/users',
            contentType: 'application/json',
            data: JSON.stringify(data)
        }).done(function(result) {
            alert('비밀번호 변경이 완료되었습니다.\n다시 로그인해주세요.');
            location.href = '/login'
        }).fail(function(xhr, status, error) {

            var result = xhr.responseJSON;
            common.serverError(result, false);

            if(!!result.cause){
                var code = result.cause.code;
                alert('비밀번호 변경에 실패하였습니다.');
            }
        }).always(function(){
            common.hideLoadingBar();
        });

        return false;
    });

    var diffPassword = function(){
        
    }

    var matchPassword = function(){
        var confirmPassword = document.getElementById('newConfirmPassword');
        if($('#password').val() != $('#newConfirmPassword').val()) {
            confirmPassword.setCustomValidity('패스워드가 동일하지 않습니다.');
        } else {
            confirmPassword.setCustomValidity('');
        }
    }

    var matchPassword = function(){
        var confirmPassword = document.getElementById('newConfirmPassword');
        if($('#newPassword').val() != $('#newConfirmPassword').val()) {
            confirmPassword.setCustomValidity('패스워드가 동일하지 않습니다.');
        } else {
            confirmPassword.setCustomValidity('');
        }
    }

    $('#newPassword').change(function(){
        matchPassword();
    });

    $('#newConfirmPassword').keyup(function(){
        matchPassword();
    });

});
