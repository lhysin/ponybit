var myPage = {};
/**
 * screen init
 */
$(document).ready(function(){

    var loginUser = myPage.loginUser;
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
        var loginUser = myPage.loginUser;
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

    if(myPage.userLevel > 1){
        $('#kakaoTab').html('<div class="alert alert-success" style="margin-top: 15px;margin-bottom:15px">카카오톡 인증이 완료되었습니다.</div>');
        return false;
    }

    myPage.authCheckFunction = function(code){

        common.showLoadingBar(true, true);

        $.ajax({
            type: 'get',
            url: '/api/v1.0/kakaos/send/msg',
            data : {code : code}
        }).done(function(result) {
            alert('카카오톡에서 나에게 보내는 메세지를 확인해주세요.');
//            location.href = '/presale';
        }).fail(function(xhr, status, error) {

            var result = xhr.responseJSON;
            common.serverError(result, false);

            if(!!result.cause){
                var code = result.cause.code;
                if(code === 2005){
                    alert('카카오톡 계정이 아닙니다.\n다른 계정으로 다시 시도해주세요.');
                    Kakao.Auth.loginForm({
                        success: function(authObj) {
                            myPage.kakaoDynamicAuthCheck();
                        },
                        fail : function(){
                            alert('카카오톡 로그인에 실패하였습니다.');
                        }
                    });
                    return false;
                } else if(code === 2007){
                    alert('이미 인증된 카카오톡 계정입니다.\n다른 계정으로 다시 시도해주세요.');
                    Kakao.Auth.loginForm({
                        success: function(authObj) {
                            myPage.kakaoDynamicAuthCheck();
                        },
                        fail : function(){
                            alert('카카오톡 로그인에 실패하였습니다.');
                        }
                    });
                    return false;
                } else if(code === 2008){
                    alert('이미 카카오톡 본인인증이 완료 되었습니다.');
                    location.href = '/mypage';
                    return false;
                }
//                else if(code === 2009){
//                    alert('한국 국적의 카카오톡 계정만 인증 가능합니다.\n다른 계정으로 다시 시도해주세요.');
//                    Kakao.Auth.loginForm({
//                        success: function(authObj) {
//                            var accessToken = authObj.access_token;
//                            myPage.authCheckFunction(accessToken);
//                        },
//                        fail : function(){
//                            alert('카카오톡 로그인에 실패하였습니다.');
//                        }
//                    });
//                    return false;
//                } 
                else if(code === 2015){
                    alert('하루 요청횟수를 초과하였습니다.\n자정이 지난뒤 다시 시도해 주세요.');
                    return false;
                }
            }
            alert('카카오톡 계정 인증에 실패하였습니다.');
        }).always(function(){
            common.hideLoadingBar();
        });
    }

    // 사용할 앱의 JavaScript 키를 설정해 주세요.
    Kakao.init('a68c0b4f9d0f27d7e098385ad9b7eb9b');
    $('#kakao-login-btn').click(function(){
        myPage.kakaoDynamicAuthCheck();
    })

    myPage.kakaoDynamicAuthCheck = function(){
        var url = 'https://kauth.kakao.com/oauth/authorize?client_id=b0deaef9cc7c745e1b222d33d8adbfbd&redirect_uri=' + myPage.baseUrl + '/continue&response_type=code&scope=talk_message';
        window.open(url, '카카오톡 인증', 'width=554,height=616');
    }
//    Kakao.Auth.loginForm({
//        success: function(authObj) {
//        	var url = 'https://kauth.kakao.com/oauth/authorize?client_id=b0deaef9cc7c745e1b222d33d8adbfbd&redirect_uri=' + myPage.baseUrl + '/continue&response_type=code&scope=talk_message';
//        	var ret = window.open(url, '카카오톡 인증', 'width=570,height=350');
//            myPage.authCheckFunction(accessToken);
//        },
//        fail : function(){
//            alert('카카오톡 로그인에 실패하였습니다.');
//        }
//    });
//    // 카카오 로그인 버튼을 생성합니다.
//    Kakao.Auth.createLoginButton({
//      container: '#kakao-login-btn',
//      success: function(authObj) {
//          var accessToken = authObj.access_token;
//          myPage.authCheckFunction(accessToken);
//      },
//      fail: function(err) {
//          alert('카카오톡 로그인에 실패하였습니다.');
//      }
//    });

});

