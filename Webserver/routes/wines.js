var mongo = require('mongodb');
var express = require('express');
var fs = require('fs');
var mongoose = require('mongoose');

var imgPathMax = 'img/avatar.png';
var imgPathMax2 = 'img/avatar2.png';

var Server = mongo.Server,
    Db = mongo.Db,
    BSON = mongo.BSONPure;

var server = new Server('localhost', 27017, {auto_reconnect: true});

//db = new Db('medications', server);
userDb = new Db('patients', server);

userDb.open(function(err, userDb) {
    if(!err) {
        console.log("Connecting to 'patients' database...");
        userDb.collection('patients', {strict:true}, function(err, collection) {
            if (err) {
                console.log("The database 'patients' doesn't exist. Creating it with sample data...");
                populatePatientsDB();
            }else{
			console.log("Connected to 'patients' database.");
			}
        });
    }else{
		 console.log(err.message);
	}
});

exports.getAllPatients = function(req, res) {
    userDb.collection('patients', function(err, collection) {
        collection.find().toArray(function(err, items) {
            res.send(items);
        });
    });
};

exports.findPatientByEC = function(req, res) {
    var ec = req.params.ec;
    console.log('Retrieving patient: ' + ec);
    userDb.collection('patients', function(err, collection) {
        collection.findOne({'ec':ec}, function(err, item) {
			if(!item){
				console.log("Nothing found");
			}
			
            res.send(item);
        });
    });
};

exports.registerPatient = function(req, res) {
    var patient = req.body;
    console.log('Adding patient: ' + JSON.stringify(wine));
    userDb.collection('patients', function(err, collection) {
        collection.insert(patient, {safe:true}, function(err, result) {
            if (err) {
                res.send({'error':'An error has occurred'});
            } else {
                console.log('Patient registered successfully: ' + JSON.stringify(result[0]));
                res.send(result[0]);
            }
        });
    });
}

/*db.open(function(err, db) {
    if(!err) {
        console.log("Connected to 'medications' database");
        db.collection('medications', {strict:true}, function(err, collection) {
            if (err) {
                console.log("The database 'medications' doesn't exist. Creating it with sample data...");
                populateDB();
            }
        });
    }else{
		 console.log(err.message);
	}
});
 
exports.findById = function(req, res) {
    var id = req.params.id;
    console.log('Retrieving medications: ' + id);
    db.collection('medications', function(err, collection) {
        collection.findOne({'_id':new BSON.ObjectID(id)}, function(err, item) {
            res.send(item);
        });
    });
};
 
exports.findAll = function(req, res) {
    db.collection('medications', function(err, collection) {
        collection.find().toArray(function(err, items) {
            res.send(items);
        });
    });
};
 
exports.addWine = function(req, res) {
    var wine = req.body;
    console.log('Adding wine: ' + JSON.stringify(wine));
    db.collection('medications', function(err, collection) {
        collection.insert(wine, {safe:true}, function(err, result) {
            if (err) {
                res.send({'error':'An error has occurred'});
            } else {
                console.log('Success: ' + JSON.stringify(result[0]));
                res.send(result[0]);
            }
        });
    });
}
 
exports.updateWine = function(req, res) {
    var id = req.params.id;
    var wine = req.body;
    console.log('Updating wine: ' + id);
    console.log(JSON.stringify(wine));
    db.collection('medications', function(err, collection) {
        collection.update({'_id':new BSON.ObjectID(id)}, wine, {safe:true}, function(err, result) {
            if (err) {
                console.log('Error updating wine: ' + err);
                res.send({'error':'An error has occurred'});
            } else {
                console.log('' + result + ' document(s) updated');
                res.send(wine);
            }
        });
    });
}
 
exports.deleteWine = function(req, res) {
    var id = req.params.id;
    console.log('Deleting wine: ' + id);
    db.collection('medications', function(err, collection) {
        collection.remove({'_id':new BSON.ObjectID(id)}, {safe:true}, function(err, result) {
            if (err) {
                res.send({'error':'An error has occurred - ' + err});
            } else {
                console.log('' + result + ' document(s) deleted');
                res.send(req.body);
            }
        });
    });
}
 
/*--------------------------------------------------------------------------------------------------------------------*/
// Populate database with sample data -- Only used once: the first time the application is started.
// You'd typically not find this code in a real-life app, since the database would already exist.
var populatePatientsDB = function() {
 
	console.log("Populating 'patients' database");
    var patients = [
    {
		ec: "123456789",
        name: "Max Mustermann",
        birthday: "10-01-1990",
		address: "Softwarepark 11 4132 Hagenberg",
    },
    {
		ec: "987654321",
        name: "Maria Musterfrau",
        birthday: "15-04-1988",
		address: "Softwarepark 11 4132 Hagenberg",
    }];
 
    userDb.collection('patients', function(err, collection) {
        collection.insert(patients, {safe:true}, function(err, result) {});
    });
 
};