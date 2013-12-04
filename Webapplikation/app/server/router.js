
var CT = require('./modules/country-list');
var AM = require('./modules/account-manager');
var WM = require('./modules/web-manager');

var patientEC = undefined;
var pageIdx = undefined;
var planName = undefined;

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

	    	WM.getPlansForPatient(patient.ec, function(err, plans){
	    		res.render('details', {
					udata : req.session.user,
					pdata : patient,
					plandata : JSON.stringify(plans)
				});
	    	});
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

			WM.getAllMedications(function(err,items){

				var medications = null;

				if(!err){
	    			medications = items;
	    			console.log("Medications received");
	    		}else{
	    			console.log("Error retreiving medication information");
	    		}

	    		// Load plan
	    		if(pageIdx == 1){
	    			WM.getMedicationPlanByName(planName, function(err, item){
		    			res.render('medicationplan', {
							udata : req.session.user,
							page : pageIdx,
							mdata : JSON.stringify(medications),
							plandata : item
						});
	    			});
	    		}else{
					res.render('medicationplan', {
						udata : req.session.user,
						page : pageIdx,
						mdata : JSON.stringify(medications),
						plandata : undefined
					});
				}
			});
	});

	app.post('/medicationplan', function(req, res) {
		console.log("Create plan");
		var plan = req.body;

		WM.createMedicationPlan(plan, function(err, response){
			if(response == null){
				console.log("error occurred");
				res.send('error',400);
			}else{
				console.log("success");
				res.send('ok',200);
			}
		});
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
	    				console.log("Patients received");
	    			}else{
	    				console.log("Error retreiving patient information");
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