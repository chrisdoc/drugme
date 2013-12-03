
function DetailsController()
{

// bind event listeners to button clicks //
	var that = this;
	
// handle user logout //
	$('#btn-logout').click(function(){ that.attemptLogout(); });
	$('#btn-new-plan').click(function(){that.showMedicationPlanPage();});
	$('#btn-confirm').click(function(){that.showConfirmationAlert();});

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

	this.showMedicationPlanPage = function()
	{
		var that = this;
		$.ajax({
			url: "/medicationplan",
			type: "GET",
			success: function(data){
				window.location.href = '/medicationplan';
			},
			error: function(jqXHR){
				console.log(jqXHR.responseText+' :: '+jqXHR.statusText);
			}
		});
	}
	
	this.showConfirmationAlert = function(){
		$('.modal-alert').modal({ show : false, keyboard : false, backdrop : 'static' });
		$('.modal-alert .modal-header h3').text('Success');
		$('.modal-alert .modal-body p').html("All currently active medication plans have been forwarded to patient ''");
		$('.modal-alert').modal('show');
		$('.modal-alert button').click(function(){window.location.href = '/';})
	}

	this.showLockedAlert = function(msg){
		$('.modal-alert').modal({ show : false, keyboard : false, backdrop : 'static' });
		$('.modal-alert .modal-header h3').text('Success!');
		$('.modal-alert .modal-body p').html(msg);
		$('.modal-alert').modal('show');
		$('.modal-alert button').click(function(){window.location.href = '/home';})
		setTimeout(function(){window.location.href = '/';}, 3000);
	}
}

DetailsController.prototype.onUpdateSuccess = function()
{
	$('.modal-alert').modal({ show : false, keyboard : true, backdrop : true });
	$('.modal-alert .modal-header h3').text('Success!');
	$('.modal-alert .modal-body p').html('Your account has been updated.');
	$('.modal-alert').modal('show');
	$('.modal-alert button').off('click');
}
