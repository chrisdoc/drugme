
var CT = require('./modules/country-list');
var AM = require('./modules/account-manager');
var WM = require('./modules/web-manager');
var gcm = require('node-gcm');

var patientEC = undefined;
var pageIdx = undefined;
var planName = undefined;
var patientIdentifier = undefined;

module.exports = function(app) {

// main login page //

	app.get('/', function(req, res){
		// check if the user's credentials are saved in a cookie //
		if (req.cookies.user == undefined || req.cookies.pass == undefined){
			res.render('login', { title: 'Hello - Please Login To Your Account' });
		}	else{
			// attempt automatic login //
			AM.autoLogin(req.cookies.user, req.cookies.pass, function(o){

				if (o != null){
				    req.session.user = o;
					res.redirect('/home');
				}	else{
					res.render('login', { title: 'Hello - Please Login To Your Account' });
				}
			});
		}
	});
	
	app.post('/', function(req, res){
		AM.manualLogin(req.param('user'), req.param('pass'), function(e, o){

			if (!o){
				res.send(e, 400);
			}	else{
			    req.session.user = o
				if (req.param('remember-me') == 'true'){
					res.cookie('user', o.user, { maxAge: 900000 });
					res.cookie('pass', o.pass, { maxAge: 900000 });
				}
				res.send(o, 200);
			}
		});
	});
	
	// patient details page //
	app.get('/details', function(req, res) {

		if(req.param('ec') != undefined){
			patientEC = req.param('ec');
		}

		WM.getAllPatients(function(err,items){
	    	var patient = items[patientEC];

	    	if(req.session.user == undefined){
	    		res.redirect('/home');
	    	}else{
		    	WM.getPlansForPatient(patient.ec, function(err, plans){
		    		res.render('details', {
						udata : req.session.user,
						pdata : patient,
						plandata : JSON.stringify(plans)
					});
		    	});
		    }
		});
	});
	
	app.post('/details', function(req, res) {

		// User
		var user = {
			name : req.param('patientName'),
			ec : req.param('patientEc'),
			birthday : req.param('patientBirthday'),
			address : req.param('patientAddress'),
			apikey : req.param('patientApi')
		}

		var message = new gcm.Message({
		    collapseKey: 'demo',
		    delayWhileIdle: true,
		    timeToLive: 3,
		});

		var sender = new gcm.Sender('AIzaSyDtpBF0NjtQSaCSQNF6EhB2zEqW9HJrJiA');
		var registrationIds = [];

		message.addDataWithKeyValue('method','updateMedication');
		message.addDataWithKeyValue('url','http://kieslich.tk:3000/medicationplans/' + user.ec);

		message.collapseKey = 'demo';
		message.delayWhileIdle = true;
		message.timeToLive = 3;

		// At least one required
		//nexus4
		registrationIds.push(user.apikey);
		//emulator
		registrationIds.push('APA91bHI0cyBMnqsNtdNx9ED7qH7nPHcQYntGQmXDgrLXPJTxQllYofsuY3_eTTWE_cxjDv95EHdXJJvqbHfO4Lxfr8zLxLpkfNQIMyfezCAj7uPDJuzsW7LqtPH7_CUnJ-JDMCFcUiEzRY4WRL5-KYD6afU_NQeFQ')

		/**
		 * Params: message-literal, registrationIds-array, No. of retries, callback-function
		 **/
		sender.send(message, registrationIds, 4, function (err, result) {});

		WM.updatePatient(user, function(err, response){
				if(response == null){
					res.send('error',400);
				}else{
					res.send('ok',200);
				}
			});
	});

	// medication plan page //
	app.get('/medicationplan', function(req, res) {


			if(req.param('page') != undefined){
				pageIdx = req.param('page');
			}

			if(req.param('plan') != undefined){
				planName = req.param('plan');
			}

			if(req.param('ec') != undefined){
				patientIdentifier = req.param('ec');
			}

			WM.getAllMedications(function(err,items){

				var medications = null;

				if(!err){
	    			medications = items;
	    		}else{
	    		}

	    		// Load plan
	    		if(pageIdx == 1){

	    			WM.getMedicationPlanByName(planName, function(err, item){

	    				if(item == undefined){
							res.redirect('/home');
	    				}else{	
			    			res.render('medicationplan', {
								udata : req.session.user,
								page : pageIdx,
								mdata : JSON.stringify(medications),
								patient : patientIdentifier,
								plandata : item
							});
		    			}
	    			});
	    		}else{
					res.render('medicationplan', {
						udata : req.session.user,
						page : pageIdx,
						mdata : JSON.stringify(medications),
						patient : patientIdentifier,
						plandata : undefined
					});
				}
			});
	});

	app.post('/medicationplan', function(req, res) {

		var intakeAmount = '';
		var intakeFrequency = '';

		if(req.param('page') == '2'){
			WM.deleteMedicationPlan(req.param('plan'), function(err, response){
		    	if(response == null){
					res.send('error',400);
				}else{
					res.send('ok',200);
				}	
	    	});
		}

		// Frequency
		if(req.param('monday') == 'true'){
			intakeFrequency += '1-';
		}else{
			intakeFrequency += '0-';
		}

		if(req.param('tuesday') == 'true'){
			intakeFrequency += '1-';
		}else{
			intakeFrequency += '0-';
		}

		if(req.param('wednesday') == 'true'){
			intakeFrequency += '1-';
		}else{
			intakeFrequency += '0-';
		}

		if(req.param('thursday') == 'true'){
			intakeFrequency += '1-';
		}else{
			intakeFrequency += '0-';
		}

		if(req.param('friday') == 'true'){
			intakeFrequency += '1-';
		}else{
			intakeFrequency += '0-';
		}

		if(req.param('saturday') == 'true'){
			intakeFrequency += '1-';
		}else{
			intakeFrequency += '0-';
		}

		if(req.param('sunday') == 'true'){
			intakeFrequency += '1';
		}else{
			intakeFrequency += '0';
		}

		// Intake
		if(req.param('morning') == 'true'){
			intakeAmount += req.param('amountMorning') + '-';
		}else{
			intakeAmount += '0-';
		}

		if(req.param('noon') == 'true'){ 
			intakeAmount += req.param('amountNoon') + '-';
		}else{
			intakeAmount += '0-';
		}

		if(req.param('evening') == 'true'){
			intakeAmount += req.param('amountEvening');
		}else{
			intakeAmount += '0';
		}

		// Plan
		var plan = {
			name : req.param('planName'),
			patient : req.param('patient'),
			medication : req.param('medication'),
			type : req.param('medicationType'),
			intake : intakeAmount,
			frequency : intakeFrequency,
			startdate : req.param('startDate'),
			enddate : req.param('endDate'),
			info : req.param('intake')
		}

		// Either create or update the medication plan
		if(req.param('page') == '0'){
			WM.createMedicationPlan(plan, function(err, response){
				if(response == null){
					res.send('error',400);
				}else{
					res.send('ok',200);
				}
			});
		}else if(req.param('page') == '1'){
			WM.updateMedicationPlan(plan, function(err, response){
				if(response == null){
					res.send('error',400);
				}else{
					res.send('ok',200);
				}
			});
		}
	});
	
// logged-in user homepage //
	app.get('/home', function(req, res) {

	    if (req.session.user == null){
	// if user is not logged-in redirect back to login page //
	        res.redirect('/');
	    }   else{
	    		WM.getAllPatients(function(err,items){

	    			var patients = null;

	    			if(!err){
	    				patients = items;
	    			}

	    			res.render('home', {
						countries : CT,
						udata : req.session.user,
						pdata : JSON.stringify(patients)
					});

	    		});
	    }
	});
	
	app.post('/home', function(req, res){

		if (req.param('user') != undefined) {
			AM.updateAccount({
				user 		: req.param('user'),
				name 		: req.param('name'),
				country 	: req.param('country'),
				pass		: req.param('pass')
			}, function(e, o){
				if (e){
					res.send('error-updating-account', 400);
				}	else{
					req.session.user = o;
			// update the user's login cookies if they exists //
					if (req.cookies.user != undefined && req.cookies.pass != undefined){
						res.cookie('user', o.user, { maxAge: 900000 });
						res.cookie('pass', o.pass, { maxAge: 900000 });	
					}
					res.send('ok', 200);
				}
			});
		}	else if (req.param('logout') == 'true'){
			res.clearCookie('user');
			res.clearCookie('pass');
			req.session.destroy(function(e){ res.send('ok', 200); });
		}
	});
	
// creating new accounts //
	app.get('/signup', function(req, res) {
		res.render('signup', {  title: 'Signup', countries : CT });
	});
	
	app.post('/signup', function(req, res){
		AM.addNewAccount({
			name 	: req.param('name'),
			user 	: req.param('user'),
			pass	: req.param('pass'),
			country : req.param('country')
		}, function(e){
			if (e){
				res.send(e, 400);
			}	else{
				res.send('ok', 200);
			}
		});
	});

// view & delete accounts //
	
	app.post('/delete', function(req, res){
		AM.deleteAccount(req.body.id, function(e, obj){
			if (!e){
				res.clearCookie('user');
				res.clearCookie('pass');
	            req.session.destroy(function(e){ res.send('ok', 200); });
			}	else{
				res.send('record not found', 400);
			}
	    });
	});

	
	app.get('*', function(req, res) { res.render('404', { title: 'Page Not Found'}); });

};