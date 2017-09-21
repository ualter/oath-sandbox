
var https           = require('https');
var express         = require('express');
var querystring     = require('querystring');
var Promise         = require('promise');
var pug             = require('pug');
var app             = express();
var htmlPugFunction = pug.compileFile('./views/template.pug');

app.disable('view cache');

app.get('/oauth2callback', function(req,res) {
	console.log('\n>>>**************************************');
	console.log('Request received...\n');
	res.writeHead(200, {'Content-Type': 'text/html;charset=UTF-8'});

	// Define the Gmail user inbox fixed, not necessary dinamically right now
	var userEmail         = 'ualter.junior@gmail.com';
	// Receive the authrorization code given by the user's consent, that's me!
	var authorizationCode = req.query.code;
	console.log('Authorization Code.....:' +  req.query.code.substring(0,25) + '...');

	// Perform a HTTP POST Request to retrieve the Access Token using the given authorization code
	retrieveAccessToken(authorizationCode, function(resultRetrieveAccessToken) {
		var jsonToken = JSON.parse(resultRetrieveAccessToken);  
		console.log('Access Token...........:' + jsonToken.access_token.substring(0,70) + '...');

		// Perform a HTTP GET Request to retrieve the list of User's Gmail Inbox Messages
		listUserGmailMailbox(userEmail,jsonToken.access_token,function(resultListUserGmailMailbox) {
			var messages = JSON.parse(resultListUserGmailMailbox).messages; 
			console.log('Total Inbox Messages...:' + messages.length);


			// Using Promise object to collect all the results of the all asynchronous operation of reading the email message details info
			Promise.all(messages.map( function getMessage(msg) {
				return new Promise( function (resolve, reject) {
					// Perform a HTTP GET Request to retrieve the data for each of the messages retrieved at the User's Gmail Inbox 
					readUserGmailMailboxMessage(userEmail,jsonToken.access_token,msg.id,function(resultReadUserGmailMailboxMessage) {
						resolve( JSON.parse(resultReadUserGmailMailboxMessage) );
					});

				});
			})).then( function(listMessages) {
				var tableMessages = [];

				listMessages.forEach ( m => {
					var from    = m.payload.headers.find(header => header.name === 'From');
					var subject = m.payload.headers.find(header => header.name === 'Subject');

					console.log( '   --> Message.Id: ' + m.id);

					var messageLine        = {};
					messageLine["id"]      = m.id;
					messageLine["from"]    = (from    != undefined ? from.value    : "");
					messageLine["subject"] = (subject != undefined ? subject.value : "");
					tableMessages.push(messageLine);
				});
				
				// Using Node JS Pug Template to render an "friendly" HTML view
				var html = htmlPugFunction({
				  name:userEmail, messageList:tableMessages
				});
				res.end(html);

				console.log('\nResponse sent...');
				console.log('<<<**************************************');
			});

		});
	});
}).listen(8080);

// HTTP POST Request to retrieve a Access Token 
// with the "API Server" (Authorization Server)
function retrieveAccessToken(authorizationCode, callBack) {
	var postData = querystring.stringify({
	    'code'         : authorizationCode,
	    'client_id'    : '?',
	    'client_secret': '?',
	    'redirect_uri' : 'http://localhost:8080/oauth2callback',
	    'grant_type'   : 'authorization_code'
	});

	var postOptions = {
      host: 'www.googleapis.com',
      port: '443',
      path: '/oauth2/v4/token',
      method: 'POST',
      headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
          'Content-Length': Buffer.byteLength(postData)
      }
	};

	var postReq = https.request(postOptions, function(resAccessToken) {
      resAccessToken.setEncoding('utf8');
      resAccessToken.on('data', function (chunk) {
		  callBack(chunk);
      });
  	});
  	postReq.write(postData);
  	postReq.end();	
}

// HTTP GET Request to retrieve with the "API Server" (Resource Server) 
// a list of messages at the Inbox's "User" (Resource Owner)
function listUserGmailMailbox(user,accessToken,callBack) {
	var getOptions = {
      host: 'www.googleapis.com',
      port: '443',
      path: '/gmail/v1/users/' + user + '/messages/?q=label:inbox',
      method: 'GET',
      headers: {
          'Authorization' :'Bearer ' + accessToken  
      }
	};

	var getReq = https.request(getOptions, function(resGmailApi) {
	  var content = '';	
      resGmailApi.setEncoding('utf8');
      resGmailApi.on('data', function (chunk) {
		  content += chunk;
      });
      resGmailApi.on('end', function () {
		  callBack(content);
      });
  	});
  	getReq.end();	
}

// HTTP GET Request to retrieve with the "API Server" (Resource Server) 
// the data of a specific Gmail message of the "User" (Resource Owner)
function readUserGmailMailboxMessage(user,accessToken,id,callBack) {
	var getOptions = {
      host: 'www.googleapis.com',
      port: '443',
      path: '/gmail/v1/users/' + user + '/messages/' + id,
      method: 'GET',
      headers: {
          'Authorization' :'Bearer ' + accessToken  
      }
	};

	var getReq = https.request(getOptions, function(resGmailApi) {
	  var content = '';	
      resGmailApi.setEncoding('utf8');
      resGmailApi.on('data', function (chunk) {
		  content += chunk;
      });
      resGmailApi.on('end', function () {
		  callBack(content);
      });
  	});
  	getReq.end();	
}

console.log('');
console.log('\x1b[33m%s\x1b[0m','.........................................');
console.log('\x1b[33m%s\x1b[0m',' Ualter OAuth2 Sandbox App               ');
console.log('\x1b[33m%s\x1b[0m',' Server running at http://localhost:8080/');
console.log('\x1b[33m%s\x1b[0m','.........................................');
console.log('');
console.log('');
console.log(' Attention! This will not work as it is... ');
console.log(' I am sorry, but you have to register you own Application at Google Developer Console to create your Client Credentials.');
console.log(' Then with this, you will have all information necessary to replace those that was erased here. (For protection of the Application used in the sample)');
console.log('');
console.log('\x1b[33m%s\x1b[0m','   --> 1. client_secret');
console.log('\x1b[33m%s\x1b[0m','   --> 2. client_id');
console.log('\x1b[33m%s\x1b[0m','   --> 3. redirect_uri');
console.log('');
console.log('');
console.log('  Goes to the lines 78, 79 and 80...  and there you insert these info, and you are ready to go.');
console.log('');
console.log('  Any doubt how to do this?');
console.log('  Goes to the http://ualterazambuja.com and check the Article Part I regarding OAuth2, thanks! ;-)');
console.log('');
console.log('');