/** Some boilerplate stuff **/
var express = require('express');
var backbone = require('./routes/backbone');
 
var app = express();
 
app.configure(function () {
    app.use(express.logger('dev'));  
    app.use(express.bodyParser());
});

/** Patient API **/
app.get('/patients', backbone.getAllPatients);
app.get('/patients/:ec', backbone.findPatientByEC);
app.get('/patients/:ec/img', backbone.findImageByEC);
app.post('/patients', backbone.registerPatient);
app.delete('/patients/:ec', backbone.deletePatient);
app.put('/patients/:ec', backbone.updatePatient);

/** Medication API **/
app.get('/medications', backbone.getAllMedications);
app.get('/medications/:name', backbone.findMedicationByName);
app.get('/medications/:name/img', backbone.getMedicationImageByName);

/** Medication plan API **/
app.get('/medicationplans', backbone.getAllMedicationPlans);
app.get('/medicationplans/:patient', backbone.getMedicationPlanForUser);

/** Start listening on port 3000 **/
app.listen(3000);
console.log('Listening on port 3000 ...');