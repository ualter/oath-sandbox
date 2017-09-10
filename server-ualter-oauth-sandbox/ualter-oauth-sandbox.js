
var https       = require('https');
var express     = require('express');
var querystring = require('querystring');
var Promise     = require('promise');
var app         = express();

app.get('/oauth2callback', function(req,res) {
	console.log('Receiving the Authorization Code....');
	res.writeHead(200, {'Content-Type': 'text/plain'});

	var authorizationCode = req.query.code;
	console.log('Authorization Code.....:' +  req.query.code);

	retrieveAccessToken(authorizationCode, function(resultRetrieveAccessToken) {
		console.log('Access Token Response.....:\n' + resultRetrieveAccessToken);
		var jsonToken = JSON.parse(resultRetrieveAccessToken);  

		listUserGmailMailbox('ualter.junior@gmail.com',jsonToken.access_token,function(resultListUserGmailMailbox) {
			var jsonListMsgs = JSON.parse(resultListUserGmailMailbox); 
			var messages     = jsonListMsgs.messages; 

			var fn = function getMessage(msg) {
				return new Promise(function (resolve, reject){

					readUserGmailMailboxMessage('ualter.junior@gmail.com',jsonToken.access_token,msg.id,function(resultReadUserGmailMailboxMessage) {
						var jsonEmailMsg = JSON.parse(resultReadUserGmailMailboxMessage);  
						var msg = jsonEmailMsg.id;
						resolve(msg);
					});

				});
			};

			/*jsonListMsgs.messages.forEach(function (msg) {

			});*/

			var actions = messages.map(fn);
			var results = Promise.all(actions);

			results.then(function(ids) {
				console.log('Total Msgs...:' + ids.length);

				var content = ' --> \n';
				ids.forEach(function(id) {
					content += id + "\n"
				});
				res.end('ok' +  content);
			});


			//res.end('end');
		});
	});
}).listen(8080);

/*

readUserGmailMailboxMessage('ualter.junior@gmail.com',jsonToken.access_token,msg.id,function(result) {
						var jsonEmailMsg = JSON.parse(result);  
						responseContent += jsonEmailMsg.id + "-";
						//responseContent += jsonEmailMsg.snippet;
						responseContent += '\n';
						console.log("1");
					});
*/

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
      path: '/gmail/v1/users/' + user + '/messages/?maxResults=10',
      //path: '/gmail/v1/users/' + user + '/messages/',
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