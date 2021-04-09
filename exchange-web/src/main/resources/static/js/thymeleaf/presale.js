var preSale = {};
preSale.defaultPageSize = 5;
preSale.DEPOSIT = 'deposit';
preSale.WITHDRAWL = 'withdrawl';
/**
 * screen init
 */
$(document).ready(function(){

    $('#btnPreSaleModal').click(function(){
        $('#preSaleModal').modal();
    })

    if(!!preSale.ponyCoinAvailableBalance){
        var number = common.numberWithCommas(preSale.ponyCoinAvailableBalance);
        $('#ponyCoinAvailableBalance').text(number);
    }

    if(!!preSale.etherMarketCap){
        var number = common.numberWithCommas(preSale.etherMarketCap.priceKrw);
        $('#etherKrw').text(number.split('.')[0] + '원');
    }

    if(!!preSale.today){
        $('#today').text(preSale.today);
    }

    /**
     * getTransaction pageNation
     */
    preSale.getTransactions = function(page, size){
//        common.showLoadingBar(true, true);

        var params = {page : page, size : size};
//        params.q = encodeURIComponent('coinName=ponycoin');
        params.q = '';

        $.ajax({
            type: 'get',
            data : params,
            url: '/api/v1.0/transactions/manuals'
        }).done(function(result) {
            if(!result || !result.data){
                common.serverError();
                return;
            }

            var list = result.data.list;
            for(var i = 0; i < list.length; i++){
                if(!!list[i].txId && list[i].category !== 'PROMOTION' && list[i].coinName === 'ETHEREUM'){
                    var ehterSacnUrl = preSale.etherSacnUrl;
                    list[i].txId = '<a style="text-decoration:underline" target="_blank" href="' + ehterSacnUrl + list[i].txId + '">트랜잭션 확인</a>';
                } else {
                    list[i].txId = '-';
                }

                if(list[i].category === 'WITHDRAWAL'){
                    list[i].category = '출금';
                } else if(list[i].category === 'DEPOSIT'){
                    list[i].category = '입금';
                } else if(list[i].category === 'PROMOTION'){
                    list[i].category = list[i].reason;
                } else {
                    list[i].category = '-';
                }

                if(list[i].status === 'PENDING'){
                    list[i].status = 'Pending';
                } else if(list[i].status === 'APPROVAL'){
                    list[i].status = '승인';
                } else if(list[i].status === 'FAILED'){
                    list[i].status = '실패';
                } else if(list[i].status === 'CANCEL'){
                    list[i].status = '취소';
                } else {
                    list[i].status = '-';
                }

                if(list[i].coinName === 'PONYCOIN'){
                    list[i].coinName = '포니코인';
                } else if(list[i].coinName === 'BITCOIN'){
                    list[i].coinName = '비트코인';
                } else if(list[i].coinName === 'ETHEREUM'){
                    list[i].coinName = '이더리움';
                } else {
                    list[i].coinName = '-';
                }

                if(!!list[i].reqAmount){
                    list[i].reqAmount = common.numberWithCommas(list[i].reqAmount);
                } else {
                    list[i].reqAmount = '-';
                }
            }

            // create table
            $('#transactionTable').bootstrapTable('destroy');
            $('#transactionTable').bootstrapTable({
                data: list,
                formatNoMatches: function () {
                    return '<div class="text-center">트랜잭션이 존재하지 않습니다.</div>';
                }
            });
            $('#transactionTable').bootstrapTable('hideLoading');

            var $pagination = $('#pagination');
            if($pagination.data("twbs-pagination")){
                $pagination.twbsPagination('destroy');
            }
            if(list.length > 0) {
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
                        preSale.getTransactions(page, preSale.defaultPageSize);
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

    preSale.getTransactions(1, preSale.defaultPageSize);

    $('#btnDeposit').on('click', function(){
        var html = preSale.modalTemplate(preSale.DEPOSIT);
        $('#transactionsModal .modal-body').html(html);
        $('#transactionsModal').modal();
    });
    
    $('#transactionsModal').on('keyup', '#txId', function(e){
        if($(this).val() === ''){
            return false;
        }
        var isSuccess = true;
        this.setCustomValidity('');
        if($(this).val().length > 70) {
            isSuccess = false;
            this.setCustomValidity('Transaction ID is invalid');
            this.reportValidity();
        }
        common.setformGroupSuccess(isSuccess, this);
    });

    $('#transactionsModal').on('keyup', '#amount', function(e){
        if($(this).val() === ''){
            return false;
        }
        var isSuccess = true;
        this.setCustomValidity('');
        var num = Number($(this).val());
        if(num > 1000000000000000000 || num < 0){
            isSuccess = false;
            this.setCustomValidity('Amount is invalid');
            this.reportValidity();
        }
        common.setformGroupSuccess(isSuccess, this);
    });

    $('#transactionsModal').on('keyup', '#address', function(e){
        if($(this).val() === ''){
            return false;
        }
        var isSuccess = true;
        this.setCustomValidity('');
        if($(this).val().length > 50) {
            isSuccess = false;
            this.setCustomValidity('Address is invalid');
            this.reportValidity();
        }
        common.setformGroupSuccess(isSuccess, this);
    });

    common.setformGroupSuccess = function(isSuccess, target){

        var parent = $(target).parents('.form-group');

        if(isSuccess){
            parent.removeClass('has-error');
            parent.addClass('has-success');
        } else {
            parent.addClass('has-error');
            parent.removeClass('has-success');
        }
    }

    $('#transactionsModal').on('submit', '#modalForm', function(event){

        event.preventDefault();

        if(!confirm('포니코인 사전판매를 신청하시겠습니까?')){
            return false;
        }

        var data = $(this).serializeObject();
        data.coinName = 'ETHEREUM';

//        var saleAvailPonyBalance = preSale.saleAvailPonyBalance;
//        var etherPriceKrw = preSale.etherMarketCap.priceKrw;
//
//        var preSalePonyBalance =  data.amount*etherPriceKrw*preSale.ponyKrwPirce;
//
//        if(saleAvailPonyBalance < preSalePonyBalance){
//            var overBalance = preSalePonyBalance - saleAvailPonyBalance;
//            alert('포니코인 사전판매 수량이 부족합니다.\n요청 수량 : ' + overBalance);
//            return false;
//        }

        common.showLoadingBar(true, true);

        var url = null;
        var type = $(this).attr('type');
        if(type === preSale.DEPOSIT){
            url = '/api/v1.0/transactions/manual/deposit';
        }

        if(!url){
            common.serverError();
            return;
        }

        $.ajax({
            type: 'post',
            url: url,
            contentType: 'application/json',
            data: JSON.stringify(data)
        }).done(function(result) {
            if(!alert('포니코인 사전 구매 신청에 성공하였습니다.')){
                $('#transactionsModal').modal('hide');
                preSale.getTransactions(1, preSale.defaultPageSize);
            }
        }).fail(function(xhr, status, error) {
            var result = xhr.responseJSON;
            if(!!result.cause){
                var code = result.cause.code;
                if(code === 3020){
                    alert('트랜잭션 ID가 이미 존재합니다.\n다른 트랜잭션ID를 입력해주세요.');
                    return false;
                }
            }

            var result = xhr.responseJSON;
            common.serverError(result, true);

        }).always(function(){
            common.hideLoadingBar();
        });
    });
    
    $(document).on('click', '#btnAdminAddressCopy', function(){
        var $temp = $('<input>');
        $('body').append($temp);
        $temp.val(preSale.adminEtherAddress).select();
        document.execCommand('copy');
        $temp.remove();
        alert('복사되었습니다.');
    });
});

preSale.modalTemplate = function(type){

    var firstUpperCoinName = common.firstUpperCase('ponyCoin');
    var firstUpperTypeName = common.firstUpperCase(type);

    if (type === preSale.DEPOSIT){
        $('#transactionsModal .modal-title').text('포니코인 사전구매 신청서');
    }

    var html = '';
    html += '<form id="modalForm" data-toggle="validator" role="form" type="' + type + '">';
    if (type === preSale.DEPOSIT){
        html += '  <div>이더리움 입금주소<button style="margin:0 0;"class="btn info-modal-btn btn-sm float-right" id="btnAdminAddressCopy" type="button">복사하기</button></div>';
        html += '  <div id="info" class="alert alert-info" style="margin-top: 15px;;margin-bottom:15px;word-wrap: break-word">' + preSale.adminEtherAddress + '</div>';
        html += '  <div id="warn" class="alert alert-danger" style="margin-top: 15px;margin-bottom:15px">이더리움 전송 후 사전구매 신청을 진행 해주세요.</div>';
        if(!!preSale.etherMarketCap){
            var ponyCnt = Number(preSale.etherMarketCap.priceKrw.split('.')[0])/0.1
            html += '  <div id="warn" class="alert alert-secondary text-center" style="margin-top: 15px;margin-bottom:15px">이더리움 포니코인 교환비율  1 : ' + ponyCnt + '</div>';
        }
    }

    html += '    <div class="form-group row">';
    html += '        <label for="email" class="col-sm-3 col-form-label">트랜잭션ID</label>';
    html += '            <div class="col-sm-9">';
    html += '            <input type="text" class="form-control" id="txId" name="txId" placeholder="이더리움 전송 후 생성된 트랜잭션 ID" required="required">';
    html += '            </div>';
    html += '        <div class="help-block with-errors"></div>';
    html += '    </div>';
    html += '    <div class="form-group row">';
    html += '        <label for="email" class="col-sm-3 col-form-label">보내는 수량</label>';
    html += '            <div class="col-sm-9">';
    html += '                <input type=number class="form-control" id="amount" name="amount" placeholder="보내는 이더리움 수량" required="required"';
    html += '                min="0.00000001" max="100000000000000000" step="0.00000001">';
    html += '            </div>';
    html += '        <div class="help-block with-errors"></div>';
    html += '    </div>';
    html += '    <div class="form-group row">';
    html += '        <label for="email" class="col-sm-3 col-form-label">보내는 주소</label>';
    html += '            <div class="col-sm-9">';
    html += '            <input type="text" class="form-control" id="address" name="address" placeholder="보내는 이더리움 주소" required="required">';
    html += '            </div>';
    html += '        <div class="help-block with-errors"></div>';
    html += '    </div>';
    html += '  <div class="form-group">';

    if (type === preSale.DEPOSIT){
        html += '      <button type="submit" class="btn btn-pony btn-block btn-lg">사전 구매 신청하기</button>';
    }
    html += '  </div>';
    html += '</form>';

    return html;
}
