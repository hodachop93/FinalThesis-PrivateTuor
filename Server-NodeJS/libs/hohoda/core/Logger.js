/*Begin Daily log with winston*/
var winston = require('winston');
var fs = require('fs');
var _ = require('lodash');
var logDir = 'logs';
if (!fs.existsSync(logDir)) {
    fs.mkdirSync(logDir);
}
//add custom logging levels
var levels = _.clone(winston.config.syslog.levels);
var colors = _.clone(winston.config.syslog.colors);

levels.request = _.max(levels) + 1;
colors.request = 'blue';

var logger = new winston.Logger({
    emitErrs: true,
    levels: levels,
    colors: colors,
    exceptionHandlers: [
        new winston.transports.File({filename: 'logs/exceptions.log'})
    ],
    exitOnError: false
});
logger.configure({
    level: 'error',
    transports: [
        new (require('winston-daily-rotate-file'))({
            name: 'error-file',
            level: 'error',
            filename: 'logs/error-logs.log',
            json: true,
            maxsize: 5242880, //5MB
            maxFiles: 5,
            colorize: false,
            timestamp: true
        }),
        new (require('winston-daily-rotate-file'))({
            name: 'info-file',
            level: 'info',
            filename: 'logs/info-logs.log',
            json: true,
            maxsize: 5242880, //5MB
            maxFiles: 5,
            colorize: true,
            timestamp: true
        })
    ]
});

logger.info("Logg##########");

module.exports = logger;