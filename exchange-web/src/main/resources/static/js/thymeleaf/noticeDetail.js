var notice = {};
notice.defaultPageSize = 100;
/**
 * screen init
 */
$(document).ready(function(){

    if(!!notice.isAdmin){
        $('#btnNoticeRegModal').click(function(){
            $('#noticeRegModal').modal();
        })
        $('#noticeRegModal').one('show.bs.modal', function (e) {
            $('#summernote').summernote({
                height: 300,
                minHeight: 300,
                maxHeight: 300,
                focus:true,
                popover : { 
                    image : [], 
                    link : [], 
                    air : [] 
                }
            });
        });
    }


    $('#btnNoticeReg').click(function(event){

        event.preventDefault();

        if(!confirm('공지사항을 수정하시겠습니까?')){
            return false;
        }

        var title = $('#title').val();
        if(!title){
            alert('제목을 입력하세요.');
        }
        var content = $('#summernote').summernote('code');
        if(!content){
            alert('내용을 입력하세요.');
        }

        common.showLoadingBar(true, true);

        $.ajax({
            type: 'PUT',
            url: '/api/v1.0/posts',
            contentType: 'application/json',
            data: JSON.stringify({
                 postId : notice.postId
                ,title : title
                ,content : content
                ,type : 'NOTICE'
            })
        }).done(function(result) {
            if(!alert('공지사항 수정에 성공하였습니다.')){
                location.href = '/notice';
            }
        }).fail(function(xhr, status, error) {
            var result = xhr.responseJSON;
            common.serverError(result, true);

        }).always(function(){
            common.hideLoadingBar();
        });
    });

    $('#btnNoticeDel').click(function(event){

        event.preventDefault();

        if(!confirm('공지사항을 삭제하시겠습니까?')){
            return false;
        }

        common.showLoadingBar(true, true);
 
        $.ajax({
            type: 'DELETE',
            url: '/api/v1.0/posts',
            contentType: 'application/json',
            data: JSON.stringify({
                postId : notice.postId
                ,type : 'NOTICE'
            })
        }).done(function(result) {
            if(!alert('공지사항 삭제에 성공하였습니다.')){
                location.href = '/notice';
            }
        }).fail(function(xhr, status, error) {
            var result = xhr.responseJSON;
            common.serverError(result, true);
        }).always(function(){
            common.hideLoadingBar();
        });
    });
});
