var transaction = {};
transaction.ajax = {};
transaction.defaultPageSize = 5;
transaction.wallets = {};
transaction.adminWallets = {};
transaction.DEPOSIT = 'deposit';
transaction.WITHDRAWL = 'withdrawl';

/**
 * screen init
 */
$(document).ready(function(){

    common.showLoadingBar(true, false);

    var hash = location.hash;
    if(!!hash && hash.length > 1){
        hash = hash.substring(1);
    }

    $.ajax({
        type: 'get',
        url: '/api/v1.0/preloads/dashboard'
    }).done(function(result) {
        if(!result || !result.data){
            common.serverError();
            return;
        }
        result = result.data;
        if(!!result && !!result.user){
            $('#userEmail').text(result.user.email);
            $('#loginLink').attr('href', '/logout');
            $('#loginLink').text('LOGOUT');
        }
        var list = result.coinWallets;
        for(var i = 0; i < list.length; i++){
            var coinName = list[i].coin.name;
            var walletCoinName = list[i].wallet.coinName;
            var availWallet = false;
            if(coinName === walletCoinName){
                transaction.wallets[coinName] = list[i].wallet;
                availWallet = true;

                var adminWalletCoinName = list[i].adminWallet.coinName;
                if(walletCoinName === adminWalletCoinName){
                    transaction.adminWallets[coinName] = list[i].adminWallet;
                } else {
                    common.serverError();
                }
            }
            var $li = '<li coinName="' + coinName + '" avail="' + availWallet + '"><a>' + coinName + '</a></li>';
            $('#coinList').append($li);
        }

        // hash or first element click
        var $clickLi = null;
        $('#coinList li').each(function( index ) {
            if(index < 1){
                $clickLi = $(this);
            }
            if($(this).attr('coinName') === hash
                    && $(this).attr('avail') === 'true'){
                $clickLi = $(this);
                return;
            }
        });
        $clickLi.click();

    }).fail(function(xhr, status, error) {

        var result = xhr.responseJSON;
        common.serverError(result, true);

    }).always(function(){
        common.hideLoadingBar();
    });

    $('#coinList').on('click', 'li', function(event){
        event.preventDefault();

        if($(this).hasClass('active')) {
            return;
        }

        $('#coinList li').removeClass('active');
        $(this).addClass('active');

        var coinName = $(this).attr('coinname');
        transaction.currentCoinName = coinName;

        $('#avail').removeClass('alert-success alert-danger');
        var isAvail = $(this).attr('avail');
        if(isAvail === 'true') {
            $('#avail').addClass('alert-success');
            $('#avail').html('This Wallet is <strong>available.</strong>');

            transaction.ajax.getTransactions(1, transaction.defaultPageSize);
        } else {
            $('#avail').addClass('alert-danger');
            $('#avail').html('This Wallet is <strong>unavailable.</strong>');

            if(!$('#precreateWallet').is(':visible')){
                $('#precreateWallet').show();
                $('#transactionList').hide();
            }
        }
    });

    $('#precreateWallet #btnMovePage').on('click', function(){
        $(location).attr('href', '/dashboard#' + transaction.currentCoinName);
    });

    $('#btnDeposit').on('click', function(){
        var html = transaction.modalTemplate(transaction.DEPOSIT);
        $('#transactionsModal .modal-body').html(html);
        $('#transactionsModal').modal();
    });

    $('#btnWithdrawals').on('click', function(){
        var html = transaction.modalTemplate(transaction.WITHDRAWL);
        $('#transactionsModal .modal-body').html(html);
        $('#transactionsModal').modal();
    });

    $('#transactionsModal').on('submit', '#modalForm', function(event){

        event.preventDefault();
        common.showLoadingBar(true, true);

        var data = $(this).serializeObject();
        data.coinName = transaction.currentCoinName;

        var url = null;
        var type = $(this).attr('type');
        if(type === transaction.DEPOSIT){
            url = '/api/v1.0/transactions/manual/deposit';
        } else if(type === transaction.WITHDRAWL) {
            url = '/api/v1.0/transactions/manual/withdrawl';

            var wallet = transaction.wallets[transaction.currentCoinName];
            if(!wallet){
                common.serverError();
                return;
            }
            if(data.amount > wallet.availableBalance){
                alert('Don\'t have enough Available Balance.');
                common.hideLoadingBar();
                return;
            };
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
            if(!alert(common.firstUpperCase(type) + ' request is successful.')){
                $('#transactionsModal').modal('hide');
                if(type == transaction.WITHDRAWL){
                    if(!!result.data.availableBalance){
                        transaction.wallets[transaction.currentCoinName] = result.data;
                    } else {
                        window.location.hash = '#' + transaction.currentCoinName; 
                        location.reload();
                    }
                }
                transaction.ajax.getTransactions(1, transaction.defaultPageSize);
            }
        }).fail(function(xhr, status, error) {
            // 4001 transaction already exist

            var result = xhr.responseJSON;
            common.serverError(result, true);

        }).always(function(){
            common.hideLoadingBar();
        });
    });
});

/**
 * getTransaction pageNation
 */
transaction.ajax.getTransactions = function(page, size){
    common.showLoadingBar(true, true);

    if($('#coinList li[class=active]').attr('coinName') !== transaction.currentCoinName){
        $('#tabContent').hide();
    }

    var params = {page : page, size : size};
    params.q = encodeURIComponent('coinName=' + transaction.currentCoinName);

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
            list[i].category = common.firstUpperCase(list[i].category);
            list[i].status = common.firstUpperCase(list[i].status);
        }

        // create table
        $('#transactionTable').bootstrapTable('destroy');
        $('#transactionTable').bootstrapTable({
            data: list
        });
        $('#transactionTable').bootstrapTable('hideLoading');

        var $pagination = $('#transactionList').find('#pagination');
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
                onPageClick: function(event, page){
                    transaction.ajax.getTransactions(page, transaction.defaultPageSize);
                },
            });
        }

        // first open
        if(!$('#transactionList').is(':visible')){
            $('#transactionList').show();
            $('#precreateWallet').hide();
        }
    }).fail(function(xhr, status, error) {

        var result = xhr.responseJSON;
        common.serverError(result, true);

    }).always(function(){
        common.hideLoadingBar();
    });
}

transaction.modalTemplate = function(type){

    var firstUpperCoinName = common.firstUpperCase(transaction.currentCoinName);
    var firstUpperTypeName = common.firstUpperCase(type);

    if (type === transaction.DEPOSIT){
        $('#transactionsModal .lead').text(firstUpperCoinName + ' Deposit Form');
        $('#transactionsModal .modal-title').text('Deposit');
    } else if(type === transaction.WITHDRAWL) {
        $('#transactionsModal .lead').text(firstUpperCoinName + ' Withdrawals Form');
        $('#transactionsModal .modal-title').text('Withdrawals');
    }

    var html = '';
    html += '<form id="modalForm" data-toggle="validator" role="form" type="' + type + '">';
    if (type === transaction.DEPOSIT){
        var adminWallet = transaction.adminWallets[transaction.currentCoinName];
        html += '  <div id="info" class="alert alert-info" style="margin-top: 1px;padding: 5px;margin-bottom:10px;word-wrap: break-word">Admin ' + firstUpperCoinName + ' address :\n' + adminWallet.address + '</div>';
        html += '  <div id="warn" class="alert alert-danger" style="margin-top: 1px;padding: 5px;margin-bottom:10px">Please apply after depositing to the admin address.</div>';
    } else if(type === transaction.WITHDRAWL) {
        var availableBalance = 0;
        var wallet = transaction.wallets[transaction.currentCoinName];
        if(!!wallet){
            availableBalance = wallet.availableBalance;
        }
        html += '  <div id="info" class="alert alert-info" style="margin-top: 1px;padding: 5px;margin-bottom:10px;word-wrap: break-word">Your ' + firstUpperCoinName +' available amount :\n' + availableBalance + '</div>';
        html += '  <div id="warn" class="alert alert-danger" style="margin-top: 1px;padding: 5px;margin-bottom:10px">Please apply after Withdrawals to the admin address.</div>';
    }
    if (type === transaction.DEPOSIT){
        html += '    <div class="form-group">';
        html += '        <div class="input-group">';
        html += '            <span class="input-group-addon"><i class="fa fa-outdent"></i></span>';
        html += '            <input type="text" class="form-control" id="txId" name="txId" placeholder="Transaction ID" required="required">';
        html += '        </div>';
        html += '        <div class="help-block with-errors"></div>';
        html += '    </div>';
    }
    html += '    <div class="form-group">';
    html += '        <div class="input-group">';
    html += '            <span class="input-group-addon"><i class="fa fa-outdent"></i></span>';
    html += '            <input type=number class="form-control" id="amount" name="amount" placeholder="Amount" required="required"';
    html += '            min="0.00000001" max="100000000000000000" step="0.00000001">';
    html += '        </div>';
    html += '        <div class="help-block with-errors"></div>';
    html += '    </div>';
    html += '    <div class="form-group">';
    html += '        <div class="input-group">';
    html += '            <span class="input-group-addon"><i class="fa fa-outdent"></i></span>';
    if (type === transaction.DEPOSIT){
        html += '            <input type="text" class="form-control" id="address" name="address" placeholder="From Address" required="required">';
    } else if(type === transaction.WITHDRAWL) {
        html += '            <input type="text" class="form-control" id="address" name="address" placeholder="To Address" required="required">';
    }
    html += '        </div>';
    html += '        <div class="help-block with-errors"></div>';
    html += '    </div>';
    html += '  <div class="form-group">';
    if (type === transaction.DEPOSIT){
        html += '      <button type="submit" class="btn btn-primary btn-block btn-lg">Deposit</button>';
    } else if(type === transaction.WITHDRAWL) {
        html += '      <button type="submit" class="btn btn-primary btn-block btn-lg">Withdrawals</button>';
    }
    html += '  </div>';
    html += '</form>';

    return html;
}

// validation
$(document).ready(function(){

    $('#transactionsModal').on('keyup', '#txId', function(e){
        if($(this).val() === ''){
            return false;
        }
        var isSuccess = true;
        this.setCustomValidity('');
        if($(this).val().length > 40) {
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
        if($(this).val().length > 40) {
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
});
