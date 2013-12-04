var crypto=require('crypto');
var ciphers = crypto.getCiphers(); 
var ursa = require('ursa');

var testkey2 = 'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3BjpR+CWhSU8eBSI1W2QbgfoBAL/C8Ny\n\
fVQcynyWgzNr+jUQ5bUOBOZQVlK08AxPjzmph2KbG/v9g4CfW2TYrykGOaaUqrXIW8ekOYGbw2yt\n\
bX6xvYjCy6VSEe7YlyE74Qr9TbESd2yhxB+XNT0Z72IbbQHvZ33g0QeDHtGsXJc0RkCnKtt0DXtw\n\
ed7/ORpzJlPeXhe5tPpGwVrc3OraME3wmdIY/O89AW4NO2k4nv/AgRBr93rwTSsKpcdqpmHjZqgF\n\
JG4DRzIYxP1v7uz80WS2IQarXA3KMaPfBKFOTPkB7psuAZhT7prN61lxF+ipWa2ByzBya0qIiAd+\n\
VaiJuwIDAQAB\n'


/**
* Server side method that encodes a message using RSA public encryption and AES-GCM sysmmetric encryption
*/
function encodeCipher(publicKey, msg_to_encode){
	/**	return base64Encode(encoded_cipher);
	***** Construct PEM format Key **** 
	*/
	var header = new Buffer('-----BEGIN PUBLIC KEY-----\n', 'utf8');
	var footer = new Buffer('-----END PUBLIC KEY-----\n', 'utf8');
	var body = new Buffer(publicKey, 'utf8');
	var pemPublicKey = Buffer.concat([header, body, footer]);
	var rsaPublickey = ursa.createKey(pemPublicKey);
	/**
	*********Generate Random Key and IV ********
	**/
	var aes_key = crypto.randomBytes(32);
	var iv = crypto.randomBytes(12);

	console.log(aes_key);
	console.log(iv);
	var key_iv = Buffer.concat([aes_key, iv]);

	/**
	*********Encrypt key_iv with RSA public encryption*******
	**/
	var buf1 = new Buffer(rsa_encrypt(rsaPublickey, key_iv), 'base64');

	/**
	*********Encrypt message with AES_GCM symetric encryption ****
	**/
	var buf2 = new Buffer(aes_gcm_encryption(aes_key, iv, msg_to_encode), 'base64');

	var encoded_cipher = Buffer.concat([buf1, buf2]);

	return base64Encode(encoded_cipher);
}



/**
* RSA)PKCS1_Padding Encryption Method
*/
function rsa_encrypt(key, msg){
	var inBuf = new Buffer(msg, 'utf8');
	var encrypted = key.encrypt(inBuf, 'utf8', 'base64', ursa.RSA_PKCS1_PADDING);
	return encrypted;
}

/**
* AES-GCM Encryption
*/
function aes_gcm_encryption(key, iv, msg){
	var inBuf = new Buffer(msg, 'utf8');
	var gcm = require('node-aes-256-gcm');
	var gcmoutput = gcm.encrypt(key, iv, inBuf, new Buffer([]));
	//var cipher = crypto.createCipheriv('id-aes256-GCM' , key, iv);
	//cipher.setAutoPadding(false);
	var x = new Buffer(gcmoutput.ciphertext, 'base64');
	var y = new Buffer(gcmoutput.auth_tag, 'base64');
	return Buffer.concat([x,y]);
}

function base64Encode(unencoded) {
	return new Buffer(unencoded).toString('base64');
}
function base64Decode(encoded) {
	return new Buffer(encoded, 'base64').toString('utf8');
}
