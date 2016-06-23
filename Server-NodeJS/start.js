require("babel-register");
require("babel-polyfill");

var express = require("express");
var bootstrap = require("./bootstrap/bootstrap");

var fs = require('fs');
/*var flash = require('connect-flash');
var passport = require('passport');
var expressSession = require('express-session');
var rememberme = require('./web/middlewares/remember-me');*/

var app = express();

bootstrap(app);

//var webApp = require("./web/app");

//job kue
var queue = require('./libs/hohoda/queue/QueueService');

//rememberme(webApp);

//required for passport
/*app.use(flash());
app.use(expressSession({
    secret: 'ilovescotchscotchyscotchscotch',
    resave: true,
    saveUninitialized: true
}));
app.use(passport.initialize());
app.use(passport.session()); */// persistent login sessions


app.use(require("./api/app"));
//app.use(webApp);

var port = process.env.PORT || 8080;
app.set("port", port);
if (!module.parent) {
    app.listen(port, function () {
        console.log('Server is running on ' + port);
    });
}

