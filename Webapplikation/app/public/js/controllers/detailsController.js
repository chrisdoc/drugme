
function DetailsController()
{

// bind event listeners to button clicks //
	var that = this;

// handle user logout //
	$('#btn-logout').click(function(){ that.attemptLogout(); });
	$('#btn-new-plan').click(function(){that.showMedicationPlanPage(0, 0);});
	$('#btn-sync').click(function(){that.showConfirmationAlert();});

	$('planItem').click(function(){
		var idx = this.id;
		that.showMedicationPlanPage(1, idx);
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

	this.showMedicationPlanPage = function(pageIndex, name)
	{
		var that = this;
		$.ajax({
			url: "/medicationplan",
			type: "GET",
			data: {page : pageIndex, plan : name},
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
		$('.modal-alert .modal-body p').html("All currently active medication plans have been forwarded to patient '" + $('#patientContainer #labelPatient').text() + "'");
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
