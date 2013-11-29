var gcm = require('node-gcm');

// or with object values
var message = new gcm.Message({
    collapseKey: 'demo',
    delayWhileIdle: true,
    timeToLive: 3,
    
});
var urls=['https://gist.github.com/chriskies/7672921/raw/370f7fe03d6fc6f35e16ac1538253f544247a824/medication.json','https://gist.github.com/chriskies/7679573/raw/8e5542bbec30e0e645a5aa2d0ded84ff0e5bf8df/medication2.json','https://gist.github.com/chriskies/7679583/raw/890ca9597d6004942fd951605cb1436d07f224c0/medication3.json','https://gist.github.com/chriskies/7679594/raw/medication4.json'];
var sender = new gcm.Sender('AIzaSyDtpBF0NjtQSaCSQNF6EhB2zEqW9HJrJiA');
var registrationIds = [];

message.addDataWithKeyValue('method','updateMedication');
//message.addDataWithKeyValue('url',urls[Math.floor((Math.random()*4))]);

message.addDataWithKeyValue('url','https://gist.github.com/chriskies/7679594/raw/medication4.json');

message.collapseKey = 'demo';
message.delayWhileIdle = true;
message.timeToLive = 3;
// END OPTIONAL

// At least one required
registrationIds.push('APA91bEGswndqcUNN5TPJezbYAo01xEdDtcr8hmQb5WHEO5BZSIfZK_JQ27Wh3-D9ytmNSKamk76njOljrszgqHjpbhf-SZ9PxvkWmAc2FkeQIWchB2FaLWgxaqFExc7OSfCyq_USzJQNH19toh701CASmi0_GGgRA');


/**
 * Params: message-literal, registrationIds-array, No. of retries, callback-function
 **/
sender.send(message, registrationIds, 4, function (err, result) {
    console.log(result);
});