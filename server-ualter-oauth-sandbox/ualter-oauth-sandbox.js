
var https       = require('https');
var express     = require('express');
var querystring = require('querystring');
var Promise     = require('promise');
var app         = express();

app.get('/oauth2callback', function(req,res) {
	console.log('\n>>>**************************************');
	console.log('Request received...\n');
	res.writeHead(200, {'Content-Type': 'text/plain;charset=UTF-8'});

	var userEmail         = 'ualter.junior@gmail.com';
	var authorizationCode = req.query.code;
	console.log('Authorization Code.....:' +  req.query.code);

	retrieveAccessToken(authorizationCode, function(resultRetrieveAccessToken) {
		var jsonToken = JSON.parse(resultRetrieveAccessToken);  
		console.log('Access Token...........:' + jsonToken.access_token);

		listUserGmailMailbox(userEmail,jsonToken.access_token,function(resultListUserGmailMailbox) {
			var jsonListMsgs = JSON.parse(resultListUserGmailMailbox); 
			var messages     = jsonListMsgs.messages; 
			console.log('Total Inbox Messages...:' + messages.length);

			Promise.all(messages.map( function getMessage(msg) {
				return new Promise( function (resolve, reject) {
					readUserGmailMailboxMessage(userEmail,jsonToken.access_token,msg.id,function(resultReadUserGmailMailboxMessage) {
						var jsonEmailMsg = JSON.parse(resultReadUserGmailMailboxMessage);  

						var emailMessage = jsonEmailMsg.id;
						emailMessage += ' - ';
						var subject = jsonEmailMsg.payload.headers.find(header => header.name === 'Subject');
						if (subject != undefined) {
							emailMessage += subject.value;
						}
						console.log('  ---> ' + emailMessage);
						resolve(emailMessage);
					});

				});
			})).then(function(inboxMessages) {
				var content = ' --> The ' + inboxMessages.length + ' Inbox Messages of ' + userEmail + '\n';
				inboxMessages.forEach(function(emailMessage) {
					content += emailMessage + "\n"
				});
				res.end(content);

				console.log('\nResponse sent...');
				console.log('<<<**************************************');
			});

		});
	});
}).listen(8080);

function retrieveAccessToken(authorizationCode, callBack) {
	var postData = querystring.stringify({
	    'code'         : authorizationCode,
	    'client_id'    : '378021837134-9ga1n3bkc40fasa0lgn79cpilb74lv1n.apps.googleusercontent.com',
	    'client_secret': 'baOVq7jr3pGdzzaUh0gzlnGN',
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
console.log('........................................');
console.log('Server running at http://localhost:8080/');
console.log('........................................');
console.log('');