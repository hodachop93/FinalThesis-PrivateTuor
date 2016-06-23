var gulp = require('gulp');
var nodemon = require('gulp-nodemon');
var babel = require('babel-register');
var env = require('gulp-env'); // set enviroment
var istanbul = require('gulp-babel-istanbul');


gulp.task('default', function () {
    nodemon({
        script: 'start.js',
        ext: 'js',
        env: {'NODE_ENV': 'development'},
        ignore: ['./node_modules/**']
    }).on('restart', function () {
        console.log('Restarting ....')
    });
});