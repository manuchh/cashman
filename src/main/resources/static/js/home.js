    	$(document).ready(function () {
    		var action = $('#formAction').val();
    		$('.tab-content').hide();
    		if (action == 'checkBalance') {
    			$('#checkBalanceSection').addClass('active');
    			$('#section-checkBalance').show();
    			$('#submitAction').val("checkBalance");
    		} else if (action == 'withdraw') {
    			$('#withdrawSection').addClass('active');
    			$('#section-withdrawAmount').show();
    			$('#submitAction').val("withdraw");
    		} else if (action == 'loadCash') {
    			$('#loadCashSection').addClass('active');
    			$('#section-loadCash').show();
    			$('#submitAction').val("loadCash");
    		} else {
    			$('.nav ul li:first').addClass('active');
    			$('#section-about').show();
    		}
        
	        $('.nav ul li a').click(function (event) {
	            event.preventDefault();
	            $('#messageHeading').text(" ");
	            $('.error').text(" ");
	            $('#number').val("");
	            $('#amount').val("");
	            $('.nav ul li a').removeClass('active');
	            var content = $(this).attr('href');
	            if(content == "#section-checkBalance") {
	            	$('#submitAction').val("checkBalance");
	            } else if(content == "#section-withdrawAmount") {
	            	$('#submitAction').val("withdraw");
	            } else if(content == "#section-loadCash") {
	            	$('#submitAction').val("loadCash");
	            }
	            $(this).parent().addClass('active');
	            $(this).parent().siblings().removeClass('active');
	            $(content).show();
	            $(content).siblings('.tab-content').hide();
	        });
    });