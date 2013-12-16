var mongo = require('mongodb');
var express = require('express');
var fs = require('fs');

var Server = mongo.Server,
    Db = mongo.Db,
    BSON = mongo.BSONPure;

var server = new Server('localhost', 27017, {auto_reconnect: true});

/** Database to manage the patients and the medications **/
db = new Db('drugmeDb', server);

/* establish the database connection */

db.open(function(e, db) {
	if (!e) {        
		console.log("Connecting to database...");
		
		/** Fill patients collection with dummy data if it is empty **/
        db.collection('patients', {strict:true}, function(err, collection) {
            if (err) {
                console.log("The collection 'patients' doesn't exist. Creating it with sample data...");
                populatePatientsCollection();
            }
        });
		
		/** Fill medications collection with dummy data if it is empty **/
		db.collection('medications', {strict:true}, function(err, collection) {
            if (err) {
                console.log("The collection 'medications' doesn't exist. Creating it with sample data...");
                populateMedicationsCollection();
            }
        });
		
		/** Fill medication plan collection with dummy data if it is empty **/
		db.collection('medicationplans', {strict:true}, function(err, collection) {
            if (err) {
                console.log("The collection 'medicationplans' doesn't exist. Creating it with sample data...");
                populateMedicationPlanCollection();
            }
        });
		
		console.log("Connected to database.");
	}	else{
		console.log(e.message);
	}
});

// Collections used for persistency
var patients = db.collection('patients');
var medications = db.collection('medications');
var medicationPlans = db.collection('medicationplans');

/****************************************************************************/
/****																     ****/
/****							USER API								 ****/
/****																     ****/
/****************************************************************************/

/** Get's a list of all patients **/
exports.getAllPatients = function(callback) {
   db.collection('patients', function(err, collection) {
        collection.find().toArray(function(err, items) {
			if(err){
				callback(err);
			} else{
				callback(null, items);
			}
   		});
   	});
};

/** Get's the patient which is identified by the given EC number **/
exports.findPatientByEC = function(eccard, callback) {
    var ec = eccard;

    db.collection('patients', function(err, collection) {
        collection.findOne({'ec':ec}, function(err, item) {
			if(err){
				callback(err);
			}else{
				callback(null, item);
			}
        });
    });
};

/** Get's the avatar of the patient which is identified by the given EC number **/
exports.findImageByEC = function(req, res) {
    var ec = req.params.ec;
    console.log('Retrieving patient avatar: ' + ec);
    db.collection('patients', function(err, collection) {
        collection.findOne({'ec':ec}, function(err, item) {
			if(!item){
				console.log("A patient with EC '" + ec + "' does not exist.");
				res.send("");
			}else{
				var imgPath = item.image;
				var img = fs.readFileSync(imgPath);
				res.writeHead(200, {'Content-Type': 'image/png' });
				res.end(img, 'binary');
			}
        });
    });
};

/** Registers a new patient and adds him to the database **/
exports.registerPatient = function(_eccard, _name, _apikey, callback) {
    var patient = {
		ec : _eccard,
		name : _name,
		apikey : _apikey
	}
	
	patient.image = "img/template/avatar.png";
	
	// Check if patient already exists
	db.collection('patients', function(err, collection) {
        collection.findOne({'ec':patient.ec}, function(err, item) {
			if(!item){
				 console.log('Adding patient: ' + JSON.stringify(patient));
				 db.collection('patients', function(err, collection) {
					 collection.insert(patient, {safe:true}, function(err, result) {
						if(err){
						  	callback(e);
						}else{
							callback(null, "Patient registered");
						}	
					});
				});
			}else{
				console.log("A patient with EC '" + patient.ec + "' already exists.");
				callback(null, "A patient with this name already exists.");	
			}
        });
    });
}

/** Deletes the patient which is identified by the given EC number **/
exports.deletePatient = function(req, res) {
    var ec = req.params.ec;
    console.log('Deleting patient: ' + ec);
    db.collection('patients', function(err, collection) {
        collection.remove({'ec':ec}, {safe:true}, function(err, result) {
            if (err) {
				res.writeHead(500, {'Content-Type': 'text/plain' });
				res.end({'error':'An error has occurred - ' + err});
            } else {
                console.log('Patient ' + ec + ' deleted');
                res.writeHead(200, {'Content-Type': 'text/plain' });
				res.end("Patient " + ec + " deleted.");
            }
        });
    });
}

/** Updates the information of the patient who is identified by the given EC number **/
exports.updatePatient = function(u, callback) {
    var user = u;
	
    console.log('Updating user: ' + user.ec);
    console.log(JSON.stringify(user));
    db.collection('patients', function(err, collection) {
        collection.update({'ec':user.ec}, user, {safe:true}, function(err, result) {
           			if(err){
					  	callback(e);
					}else{
						callback(null, "Plan created");
					}	
        });
    });
}

/** Populates the 'patients' collection with dummy data **/
var populatePatientsCollection = function() {
 
	console.log("Populating 'patients' collection");
	
    var patients = [
    {
		ec: "123456789",
        name: "Max Mustermann",
        birthday: "10/01/1990",
		address: "Softwarepark 11 4132 Hagenberg",
		apikey: "APA91bEGswndqcUNN5TPJezbYAo01xEdDtcr8hmQb5WHEO5BZSIfZK_JQ27Wh3-D9ytmNSKamk76njOljrszgqHjpbhf-SZ9PxvkWmAc2FkeQIWchB2FaLWgxaqFExc7OSfCyq_USzJQNH19toh701CASmi0_GGgRA",
		image: "img/users/avatar.png"
    },
    {
		ec: "987654321",
        name: "Maria Musterfrau",
        birthday: "15/04/1988",
		address: "Softwarepark 11 4132 Hagenberg",
		apikey: "APA91bEGswndqcUNN5TPJezbYAo01xEdDtcr8hmQb5WHEO5BZSIfZK_JQ27Wh3-D9ytmNSKamk76njOljrszgqHjpbhf-SZ9PxvkWmAc2FkeQIWchB2FaLWgxaqFExc7OSfCyq_USzJQNH19toh701CASmi0_GGgRA",
		image: "img/users/avatar.png"
    },
	{
		ec: "123454321",
        name: "Hannes Weltklasse",
        birthday: "20/06/1990",
		address: "Am Marktplatz 12 1337 Aschach",
		apikey: "APA91bEGswndqcUNN5TPJezbYAo01xEdDtcr8hmQb5WHEO5BZSIfZK_JQ27Wh3-D9ytmNSKamk76njOljrszgqHjpbhf-SZ9PxvkWmAc2FkeQIWchB2FaLWgxaqFExc7OSfCyq_USzJQNH19toh701CASmi0_GGgRA",
		image: "img/users/avatar.png"
    }];
 
    db.collection('patients', function(err, collection) {
        collection.insert(patients, {safe:true}, function(err, result) {});
    });
};

/****************************************************************************/
/****																     ****/
/****							MEDICATION API							 ****/
/****																     ****/
/****************************************************************************/

/** Get's a list of all medications **/
exports.getAllMedications = function(callback) {
    db.collection('medications', function(err, collection) {
        collection.find().toArray(function(err, items) {
			if(err){
				callback(err);
			} else{
				callback(null, items);
			}
		});
	});
};

/** Get's the medication identified by the given name **/
exports.findMedicationByName = function(req, res) {
    var name = req.params.name;
    console.log('Retrieving medication: ' + name);
    db.collection('medications', function(err, collection) {
        collection.findOne({'name':name}, function(err, item) {
			if(!item){
				console.log("A medication called " + name + " does not exist.");
				res.writeHead(500, {'Content-Type': 'text/plain' });
				res.end("");
			}else{
				res.send(item);
			}
        });
    });
};

/** Get's the picture of the medication identified by the given name **/
exports.getMedicationImageByName = function(req, res) {
    var name = req.params.name;
    console.log('Retrieving medication image: ' + name);
    db.collection('medications', function(err, collection) {
        collection.findOne({'name':name}, function(err, item) {
			if(!item){
				console.log("A medication called " + name + " does not exist.");
				res.writeHead(500, {'Content-Type': 'text/plain' });
				res.end("");
			}else{
				var imgPath = item.image;
				var img = fs.readFileSync(imgPath);
				res.writeHead(200, {'Content-Type': 'image/jpeg' });
				res.end(img, 'binary');
			}
        });
    });
};

/** Populates the 'medications' collection with dummy data **/
/** Eventually integrated information about field of application and side effects (NADA) **/
var populateMedicationsCollection = function() {
 
	console.log("Populating 'medications' collection");
	
    var medications = [
    {
		name: "Parkemed",
		type: "tablet",
		image: "img/medications/parkemed.jpg"
    },
    {
		name: "Albunorm",
		type: "injection",
		image: "img/medications/albunorm.jpg"
    },
	{
		name: "Athomer",
		type: "spray",
		image: "img/medications/athomer.jpg"
    },
	{
		name: "Xylosolv",
		type: "tablet",
		image: "img/medications/xylosolv.jpg"
    },
    {
		name: "Instanyl",
		type: "spray",
		image: "img/medications/instanyl.jpg"
    },
	{
		name: "Octostim",
		type: "spray",
		image: "img/medications/octostim.jpg"
    },
    {
		name: "Omnitrope",
		type: "injection",
		image: "img/medications/omnitrope.jpg"
    },
	{
		name: "Assugrin",
		type: "tablet",
		image: "img/medications/assugrin.jpg"
    },
    {
		name: "Timophtal",
		type: "fluid",
		image: "img/medications/timophtal.jpg"
    },
	 {
		name: "Omniforte",
		type: "fluid",
		image: "img/medications/omniforte.jpg"
    }];
 
    db.collection('medications', function(err, collection) {
        collection.insert(medications, {safe:true}, function(err, result) {});
    });
};

/****************************************************************************/
/****																     ****/
/****						 MEDICATION PLAN API						 ****/
/****																     ****/
/****************************************************************************/

/** Get's a list of all medication plans **/
exports.getAllMedicationPlans = function(req, res) {
    db.collection('medicationplans', function(err, collection) {
        collection.find().toArray(function(err, items) {
		
			if(!items){
				res.writeHead(500, {'Content-Type': 'text/plain' });
				res.end("");
			}else{
				res.send(items);
			}
		});
	});
};

/** Get's all medication plans assigned to a given patient **/
exports.getMedicationPlanForUser = function(req, res) {
    var ec = req.params.patient;
	
    console.log('Retrieving medication plans for patient ' + ec);
    db.collection('medicationplans', function(err, collection) {
        collection.find({'patient':ec}).toArray(function(err, items) {
			if(!items){
				console.log("There are no active medication plans for user " + ec);
				res.writeHead(500, {'Content-Type': 'text/plain' });
				res.end("");
			}else{
				res.send(items);
			}
        });
    });
};

/** Get's the medication plan which is identified by the given name */
exports.getMedicationPlanByName = function(planName, callback) {
    var name = planName;
    console.log('Retrieving medication plan: ' + name);
    db.collection('medicationplans', function(err, collection) {
        collection.findOne({'name':name}, function(err, item) {
			if(!item){
				callback(err);
			}else{
				callback(null,item);
			}
        });
    });
};

/** Get's all medication plans assigned to a given patient **/
exports.getPlansForPatient = function(eccard, callback) {
    var ec = eccard;
	
    console.log('Retrieving medication plans for patient ' + ec);
    db.collection('medicationplans', function(err, collection) {
        collection.find({'patient':ec}).toArray(function(err, items) {
			if(!items){
				callback(err);
			}else{
				callback(null, items);
			}
        });
    });
};

/** Deletes the medication plan which is identified by the given plan name **/
exports.deleteMedicationPlan = function(planName, callback) {
    var name = planName;
    console.log('Deleting medication plan: ' + name);
    db.collection('medicationplans', function(err, collection) {
        collection.remove({'name':name}, {safe:true}, function(err, result) {
            if (err) {
				callback(err);
            } else {
                callback(null, 'Plan deleted');
            }
        });
    });
}

/** Updates the medication plan identified by the given name **/
exports.updateMedicationPlan = function(medPlan, callback) {
    var plan = medPlan;
	
    console.log('Updating medication plan: ' + plan.name);
    db.collection('medicationplans', function(err, collection) {
        collection.update({'name':plan.name}, plan, {safe:true}, function(err, result) {
            if (err) {
	        	callback(err);
            } else {
                callback(null, "Plan updated");
            }
        });
    });
}

/** Updates the medication plan identified by the given name **/
exports.createMedicationPlan = function(medPlan, callback) {
    var plan = medPlan;
	
	 db.collection('medicationplans', function(err, collection) {
        collection.findOne({'name':plan.name}, function(er, item) {
			if(!item){
				console.log('Creating new medication plan: ' + plan.name);
    			console.log(JSON.stringify(plan));
				db.collection('medicationplans', function(error, collection) {
					collection.insert(plan, {safe:true}, function(e, result) {
				
					if(e){
					  	callback(e);
					}else{
						callback(null, "Plan created");
					}
				});
			});
			}else{
				console.log('A plan called ' + plan.name + ' already exists...');
				callback(er, null);
			}
        });
    });
}

/** Populates the 'medicationplan' collection with dummy data **/
var populateMedicationPlanCollection = function() {
 
	console.log("Populating 'medicationplans' collection");
	
    var medications = [
    {
		name: "ParkemedPlan",
		patient: "123456789",
		medication : "Parkemed",
		type : "tablet",
		intake: "1-1-1",
		frequency : "1-0-1-0-1-0-1",
		startdate : "02/12/2013",
		enddate : "16/12/2013",
		info : "cillum fugiat ullamco deserunt eu labore fugiat cillum est incididunt ex nulla adipisicing eiusmod magna non laboris tempor ex officia et voluptate nisi culpa est eu mollit quis magna aliquip"
    },
	{
		name: "TimophtalPlan",
		patient: "123456789",
		medication : "Timophtal",
		type : "fluid",
		intake: "0-1-1",
		frequency : "1-1-1-1-1-1-1",
		startdate : "17/12/2013",
		enddate : "27/12/2013",
		info : "cillum fugiat ullamco deserunt eu labore fugiat cillum est incididunt ex nulla adipisicing eiusmod magna non laboris tempor ex officia et voluptate nisi culpa est eu mollit quis magna aliquip"
    },
	{
		name: "TimophtalPlan2",
		patient: "987654321",
		medication : "Timophtal",
		type : "fluid",
		intake: "0-1-1",
		frequency : "0-1-1-0-1-1-0",
		startdate : "19/12/2013",
		enddate : "24/12/2013",
		info : "cillum fugiat ullamco deserunt eu labore fugiat cillum est incididunt ex nulla adipisicing eiusmod magna non laboris tempor ex officia et voluptate nisi culpa est eu mollit quis magna aliquip"
    }];
 
    db.collection('medicationplans', function(err, collection) {
        collection.insert(medications, {safe:true}, function(err, result) {});
    });
};