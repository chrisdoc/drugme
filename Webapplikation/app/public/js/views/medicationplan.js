
$(document).ready(function(){

	var dc = new MedicationPlanController();

	//Handle check events
	/*$('#morning-check').live('change', function(){
	    if($('#morning-check').is(':checked')){

	    } else {

	    }
	});

	$('#noon-check').live('change', function(){
	    if($('#noon-check').).is(':checked')){
	       
	    } else {
	       
	    }
	});

	$('#evening-check').live('change', function(){
	    if($('#evening-check').is(':checked')){
	        
	    } else {
	        
	    }
	});*/

	$("#startDate").datepicker();
	$("#endDate").datepicker();
	
	$('#name-tf').focus();

// customize the account settings form //
	$('#account-form h1').text('Account Settings');
	$('#account-form #sub1').text('Here are the current settings for your account.');
	$('#user-tf').attr('disabled', 'disabled');
	$('#account-form-btn1').html('Delete');
	$('#account-form-btn1').addClass('btn-danger');
	$('#account-form-btn2').html('Update');

// setup the confirm window that displays when the user chooses to delete their account //

	$('.modal-confirm').modal({ show : false, keyboard : true, backdrop : true });
	$('.modal-confirm .modal-header h3').text('Delete Account');
	$('.modal-confirm .modal-body p').html('Are you sure you want to delete your account?');
	$('.modal-confirm .cancel').html('Cancel');
	$('.modal-confirm .submit').html('Delete');
	$('.modal-confirm .submit').addClass('btn-danger');

})