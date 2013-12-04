
function MedicationPlanController()
{
	var pv = new PlanValidator();
// bind event listeners to button clicks //
	var that = this;
	var mondayChecked = false;
	var tuesdayChecked = false;
	var wednesdayChecked = false;
	var thursdayChecked = false;
	var fridayChecked = false;
	var saturdayChecked = false;
	var sundayChecked = false;

	var morningChecked = false;
	var noonChecked = false;
	var eveningChecked = false;

// handle user logout //
	$('#btn-logout').click(function(){ that.attemptLogout(); });

// handle checkboxes
	$('#mo-check').click(function(){
	    if($(this).is(':checked')){
	    	mondayChecked = true;
	    } else {
	    	mondayChecked = false;
	    }
	});
	
	$('#tu-check').click(function(){
	    if($(this).is(':checked')){
	    	tuesdayChecked = true;
	    } else {
	    	tuesdayChecked = false;
	    }
	});

	$('#we-check').click(function(){
	    if($(this).is(':checked')){
	    	wednesdayChecked = true;
	    } else {
	    	wednesdayChecked = false;
	    }
	});

	$('#th-check').click(function(){
	    if($(this).is(':checked')){
	    	thursdayChecked = true;
	    } else {
	    	thursdayChecked = false;
	    }
	});

	$('#fr-check').click(function(){
	    if($(this).is(':checked')){
	    	fridayChecked = true;
	    } else {
	    	fridayChecked = false;
	    }
	});

	$('#sa-check').click(function(){
	    if($(this).is(':checked')){
	    	saturdayChecked = true;
	    } else {
	    	saturdayChecked = false;
	    }
	});

	$('#su-check').click(function(){
	    if($(this).is(':checked')){
	    	sundayChecked = true;
	    } else {
	    	sundayChecked = false;
	    }
	});

	$('#morning-check').click(function(){
	    if($(this).is(':checked')){
	    	morningChecked = true;
	    } else {
	    	morningChecked = false;
	    }
	});

	$('#noon-check').click(function(){
	    if($(this).is(':checked')){
	    	noonChecked = true;
	    } else {
	    	noonChecked = false;
	    }
	});

	$('#evening-check').click(function(){
	    if($(this).is(':checked')){
	    	eveningChecked = true;
	    } else {
	    	eveningChecked = false;
	    }
	});

// Handle form submission
	$('#medication-form').ajaxForm({
		beforeSubmit : function(formData, jqForm, options){
			if (pv.validateForm(mondayChecked,tuesdayChecked,wednesdayChecked,thursdayChecked,fridayChecked,saturdayChecked,sundayChecked, morningChecked, noonChecked, eveningChecked) == false){
				return false;
			} 	else{
				return true;
			}
		},
		success	: function(responseText, status, xhr, $form){
			$('.modal-alert').modal({ show : false, keyboard : false, backdrop : 'static' });
			$('.modal-alert .modal-header h3').text('Success!');
			$('.modal-alert .modal-body p').html("A new medication plan has been created.");
			$('.modal-alert').modal('show');
			$('.modal-alert button').click(function(){window.location.href = '/details';})
		},
		error : function(e){
			$('.modal-alert').modal({ show : false, keyboard : false, backdrop : 'static' });
			$('.modal-alert .modal-header h3').text('Error');
			$('.modal-alert .modal-body p').html("A medication plan with the given name already exists.");
			$('.modal-alert').modal('show');
		}
	}); 

// handle click on list item //
	this.attemptLogout = function()
	{
		var that = this;
		$.ajax({
			url: "/home",
			type: "POST",
			data: {logout : true},
			success: function(data){
	 			that.showLockedAlert('You are now logged out.<br>Redirecting you back to the homepage.');
			},
			error: function(jqXHR){
				console.log(jqXHR.responseText+' :: '+jqXHR.statusText);
			}
		});
	}

	this.showLockedAlert = function(msg){
		$('.modal-alert').modal({ show : false, keyboard : false, backdrop : 'static' });
		$('.modal-alert .modal-header h3').text('Success!');
		$('.modal-alert .modal-body p').html(msg);
		$('.modal-alert').modal('show');
		$('.modal-alert button').click(function(){window.location.href = '/';})
		setTimeout(function(){window.location.href = '/';}, 3000);
	}

	this.showLoginError = function(t, m)
	{
		$('.modal-alert').modal({ show : false, keyboard : false, backdrop : 'static' });
		$('.modal-alert .modal-header h3').text(t);
		$('.modal-alert .modal-body p').html(m);
		$('.modal-alert').modal('show');
		$('.modal-alert button').click(function(){window.location.href = '/';})
	}
}
