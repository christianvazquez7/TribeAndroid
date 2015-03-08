var express = require('express')
var app = express()
var bodyParser = require('body-parser');
var cors = require('cors');
var ip = "you ip goes here"

app.use(cors());
app.use(bodyParser.json({limit: '50mb'}));
app.use(bodyParser.urlencoded({limit: '50mb', extended: true}));
path = require('path');
app.use(express.static(path.join(__dirname, 'public'))); 



app.post('/postCall/:caller/:receiver/:duration',function(req,res){
  	var caller = req.params.caller;
  	var receiver = req.params.receiver;
  	var duration = req.params.duration;

    console.log('----Call log received----')
    console.log(caller);
    console.log(receiver);
    console.log(duration);

  	//insert values in database here


  	//


   res.sendStatus(200);
});

app.post('/postSms/:sender/:receiver/:length',function(req,res){
  	var sender = req.params.sender;
  	var receiver = req.params.receiver;
  	var length = req.params.length;
    console.log('----Sms log received----')
    console.log(sender);
    console.log(receiver);
    console.log(length);


  	//insert values in database here


  	//

  	
   res.sendStatus(200);
});


app.post('/addFriends/:userPhone',function(req,res){
   	
   	//get primary user phone to identify
   	var myPhone = req.params.userPhone;

	//get phones of users in tribe
   	var f1Phone = req.body.f1Phone;
   	var f2Phone = req.body.f2Phone;
   	var f3Phone = req.body.f3Phone;
   	var f4Phone = req.body.f4Phone;
   	var f5Phone = req.body.f5Phone;

   	//send twilio message to ask for consent to each friend
    console.log('------registered friends-----')

    console.log(f1Phone);
    console.log(f2Phone);
    console.log(f3Phone);
    console.log(f4Phone);
    console.log(f5Phone);




   	res.sendStatus(200);
});



var server = app.listen(3020, function () {
  var host = server.address().address
  var port = server.address().port
  console.log('Listening at http://%s:%s', host, port);
});
