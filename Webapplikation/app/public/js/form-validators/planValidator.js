function PlanValidator(){

	this.loginErrors = $('.modal-alert');
	this.loginErrors.modal({ show : false, keyboard : true, backdrop : true });

	this.showLoginError = function(t, m)
	{
		$('.modal-alert .modal-header h3').text(t);
		$('.modal-alert .modal-body p').text(m);
		this.loginErrors.modal('show');
	}
}

PlanValidator.prototype.validateForm = function(mo,tu,we,th,fr,sa,su,morning,noon,evening)
{
	
	if($('#name').val() == ''){
		this.showLoginError('Whoops!', 'Please enter a name for the medication plan');
		return false;
	} else if (mo == false && tu == false && we == false && th == false && fr == false && sa == false && su == false){
		this.showLoginError('Whoops!', 'You did not select a day for medication intake');
		return false;
	} else if(morning == false && noon == false && evening == false){
		this.showLoginError('Whoops!', 'You have to specify at least one intake time');
		return false;
	} else if(morning == true && $('#inputMorning').val() == 0){
		this.showLoginError('Whoops!', "You have to specify an intake amount for 'morning'");
		return false;
	} else if(noon == true && $('#inputNoon').val() == 0){
		this.showLoginError('Whoops!', "You have to specify an intake amount for 'noon'");
		return false;
	} else if(evening == true && $('#inputEvening').val() == 0){
		this.showLoginError('Whoops!', "You have to specify an intake amount for 'evening'");
		return false;
	} else if($('#startDate').val() == ''){
		this.showLoginError('Whoops!', "You have to set a start date");
		return false;
	} else if($('#endDate').val() == ''){
		this.showLoginError('Whoops!', "You have to set a end date");
		return false;
	} else if($('#startDate').val() >= $('#endDate').val()){
		this.showLoginError('Whoops!', "The end date must be after the start date");
		return false;
	}
	
	return true;
}