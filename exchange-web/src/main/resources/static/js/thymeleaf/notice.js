var notice = {};
notice.defaultPageSize = 100;
/**
 * screen init
 */
$(document).ready(function(){

    if(!!notice.isAdmin){
        $('#btnNoticeRegModal').click(function(){
            $('#noticeRegModal').modal();
        });

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
        }).on('hidden.bs.modal', function (e) {
            $('#summernote').summernote('code', '');
        });
    }

    notice.getNotices = function(page, size){
//        common.showLoadingBar(true, true);

        var params = {page : page, size : size};
        params.q = '';

        $.ajax({
            type: 'get',
            data : params,
            url: '/api/v1.0/posts'
        }).done(function(result) {
            if(!result || !result.data){
                common.serverError();
                return;
            }

            var list = result.data.list;
            for(var i = 0; i < list.length; i++){
                list[i].title = '<a style="text-decoration:underline" target="_self" href="/notice/' + list[i].id + '">' + list[i].title + '</a>';
            }

            // create table
            $('#noticeTable').bootstrapTable('destroy');
            $('#noticeTable').bootstrapTable({
                data: list,
                formatNoMatches: function () {
                    return '<div class="text-center">공지사항이 존재하지 않습니다.</div>';
                }
            });
            $('#noticeTable').bootstrapTable('hideLoading');

            var $pagination = $('#pagination');
            if($pagination.data("twbs-pagination")){
                $pagination.twbsPagination('destroy');
            }
            if(list.length > notice.defaultPageSize) {
                $pagination.twbsPagination({
                    totalPages: result.data.totalPages,
                    startPage: result.data.page,
                    visiblePages: 4,
                    prev: '<<',
                    next: '>>',
                    first: '',
                    last: '',
                    pageClass: 'page-item',
                    onPageClick: function(event, page){
                        notice.getNotices(page, notice.defaultPageSize);
                    },
                });
            }

        }).fail(function(xhr, status, error) {

            var result = xhr.responseJSON;
            common.serverError(result, true);

        }).always(function(){
            common.hideLoadingBar();
        });
    }

    notice.getNotices(1, notice.defaultPageSize);

    $('#btnNoticeReg').click(function(event){

        event.preventDefault();

        if(!confirm('공지사항을 등록하시겠습니까?')){
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
            type: 'POST',
            url: '/api/v1.0/posts',
            contentType: 'application/json',
            data: JSON.stringify({
                title : title
                ,content : content
                ,type : 'NOTICE'
            })
        }).done(function(result) {
            if(!alert('공지사항 등록에 성공하였습니다.')){
                $('#noticeRegModal').modal('hide');
                notice.getNotices(1, notice.defaultPageSize);
            }
        }).fail(function(xhr, status, error) {
            var result = xhr.responseJSON;
            var result = xhr.responseJSON;
            common.serverError(result, true);

        }).always(function(){
            common.hideLoadingBar();
        });
    });
});
