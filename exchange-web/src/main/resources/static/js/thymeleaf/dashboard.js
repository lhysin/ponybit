var dashboard = {};

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

        var html = dashboard.getAccordion(list);
        $('#accordion').html(html);

        // hash or first element click
        var $clickA = null;
        $('#accordion .panel-title a').each(function(index) {
            if(index < 1){
                $clickA = $(this);
            }
            if($(this).attr('coinName') === hash){
                $clickA = $(this);
                return;
            }
        });
        $clickA.click();

    }).fail(function(xhr, status, error) {

        var result = xhr.responseJSON;
        common.serverError(result, true);

    }).always(function(){
        common.hideLoadingBar();
    });

    //$('#btnCreateWallet').on('click', function(event){
	$('#accordion').on('click touch', '#btnCreateWallet', function(){
        event.preventDefault();
        common.showLoadingBar(true, true);

        var coinName = $(this).attr('coinName');
        var data = {coinName : coinName};
        $.ajax({
            type: 'post',
            url: '/api/v1.0/wallets',
            contentType: 'application/json',
            data: JSON.stringify(data)
        }).done(function(result) {
            if(!result || !result.data){
                common.serverError();
                return;
            }
            var coinName = common.firstUpperCase(result.data.coinName);
            // create wallet
            if(!alert('Success for ' + coinName + ' wallet Creation.')){
                window.location.hash = '#' + coinName; 
                location.reload();
            }
        }).fail(function(xhr, status, error) {

            var result = xhr.responseJSON;
            common.serverError(result, true);

        }).always(function(){
            common.hideLoadingBar();
        });
    });
});

dashboard.getAccordion = function(list) {
    var html = '';

    if(list.length < 0){
        // TODO
    }

    for(var i = 0; i < list.length; i++){
        var coinName = list[i].coin.name;
        var availableBalance = list[i].wallet.availableBalance;
        var walletCoinName = list[i].wallet.coinName;
        var availWallet = true;
        if(coinName !== walletCoinName){
            availWallet = false;
        }

        html += '    <div class="panel panel-default">';
        html += '        <div class="panel-heading">';
        html += '            <h5 class="panel-title">';
        html += '                <a data-toggle="collapse" data-parent="#accordion" href="#collapse' + i + '" coinName="' + coinName + '">' + coinName;

        if(!!availWallet){
            html += '                <span class="badge badge-primary" style="float:right">Created</span>';
        } else {
            html += '                <span class="badge badge-primary" style="float:right">No Wallet</span>';
        }

        html += '                </a>';
        html += '            </h5>';
        html += '        </div>';
        html += '        <div id="collapse' + i + '" class="panel-collapse collapse">';
        html += '            <div class="panel-body">';

        if(!!availWallet){
            html += '            <p>Balance : ' + availableBalance + '</p>';
            html += '            <a href="/transaction#' + coinName + '">Check Transactions</a>';
        } else {
            html += '            <p>You don\'t have Wallet</p>';
            html += '            <button id="btnCreateWallet" type="button" class="btn" coinName="' + coinName + '" style="min-width:80px;padding:5px">Create Wallet</button>'
        }

        html += '            </div>';
        html += '        </div>';
        html += '    </div>';

    }

    return html;
}
