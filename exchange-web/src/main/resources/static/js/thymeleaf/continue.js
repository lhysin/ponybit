/**
 * screen init
 */
$(document).ready(function(){
    var params = common.getQueryParam();
    if(!!opener){
        if(!!opener.myPage){
            if(!!params.code){
                opener.myPage.authCheckFunction(params.code);
                self.close();
            } else {
                alert('서버오류가 발생하였습니다.\n서버 관리자에게 연락부탁드립니다.');
            }
        } else {
            location.href = '/';
        }
    } else {
        location.href = '/';
    }
})