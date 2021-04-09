var home = {};

/**
 * screen init
 */
$(document).ready(function(){

    // slider
    function slider($swiper, option){
        $swiper.each(function(){
            var $this = $(this);
            var swiper = new Swiper($this.get(0), option);
            $this.data('swiper', swiper);
            $this.data('swiper', swiper.update());
        });
    }

    slider($('.visual .swiper-container'), {
        loop: true,
        autoplay: {
            delay: 10000,
            disableOnInteraction: false,
        },
        speed: 300,
        pagination: {
            el: '.swiper-pagination',
        },
        navigation: {
            nextEl: '.swiper-button-next',
            prevEl: '.swiper-button-prev',
        },
    });

    $('.swiper-stop').click(function () {
        if($(this).hasClass('off')) {
            $(this).removeClass('off');
            $('.swiper-container').data('swiper').autoplay.start();
        } else {
            $(this).addClass('off');
            $('.swiper-container').data('swiper').autoplay.stop();
        }
        return false;
    });

    var angelFund = home.allSoldPonyBalance/home.maxSalePonyBalance * 100;
    var angelFundPer = Math.round(angelFund*10)/10;
    if(angelFundPer % 1 !== 0){
        $('#angelFundPerText').removeClass();
        $('#angelFundPerText').addClass('over-rate');
    }

    if(angelFundPer > 100){
        angelFundPer = 100;
    }

    $('#angelFundPerRate').attr('data-value', (angelFundPer/100));
    $('#angelFundPerText').html(angelFundPer + '<span class="per">%</span>');

    $('.count .circle').circleProgress({
        emptyFill: 'transparent',
        fill: {gradient: ['#ff656e', '#3670f5']}
    });

    $('#angelFund').removeClass('d-none');

    // referral url copy
    $('#btnRefCopy').click(function(){
        var $temp = $('<input>');
        $('body').append($temp);
        $temp.val($('#refUrlHead').text()).select();
        document.execCommand('copy');
        $temp.remove();
        alert('복사되었습니다.');
    });

    $(document).on('click', '#btnInfoModal', function(){
        $('#infoModal').modal();
    })

    var loginUser = home.loginUser
    if(!!loginUser){

        var objStr = localStorage.getItem(home.homeRefModalNotOpenKey);
        var obj = null;
        try {
            obj = JSON.parse(objStr);
        } catch (e){
        }

        if(!!obj){
            if(obj.expiredTimestamp > new Date().getTime()){
                $('#infoModal').remove();
                return false;
            }
        }
        localStorage.removeItem(home.homeRefModalNotOpenKey);

        var loginUser = home.loginUser;
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
            $modalHtml += '                    <div class="alert alert-info small">현재 팝업은 마이페이지에서 확인가능합니다.</div>';
            $modalHtml += '                </div>';
            $modalHtml += '                <div class="container">';
            $modalHtml += '                    <div class="checkbox">';
            $modalHtml += '                      <label><input type="checkbox" id="chkInfoModalOpen"> 오늘 하루 열지 않기</label>';
            $modalHtml += '                    <button type="button" class="btn btn-secondary float-right" data-dismiss="modal">닫기</button>';
            $modalHtml += '                    </div>';
            $modalHtml += '                </div>';
            $modalHtml += '            </div>';
            $modalHtml += '        </div>';
            $modalHtml += '      </div>';
            $modalHtml += '    </div>';
            $modalHtml += '  </div>';
            $('body').append($modalHtml);
        }

        $(document).on('click', '#chkInfoModalOpen', function(){

            if(!localStorage){
                return false;
            }

            var isChk = $(this).is(":checked");

            if(!!isChk){
                var expiredTimestamp = new Date().setHours(24, 0, 0, 0);
                var obj = {
                        userId : home.loginUser.id
                        ,expiredTimestamp : expiredTimestamp
                }
                localStorage.setItem(home.homeRefModalNotOpenKey, JSON.stringify(obj));
            } else {
                localStorage.removeItem(home.homeRefModalNotOpenKey);
            }
        });
        $('#refModal').modal();

        // referral url copy
        $(document).on('click', '#btnRefCopy', function(){
            var $temp = $('<input>');
            $('body').append($temp);
            $temp.val($('#refUrlHead').text()).select();
            document.execCommand('copy');
            $temp.remove();
            alert('복사되었습니다.');
        });
    }
});
