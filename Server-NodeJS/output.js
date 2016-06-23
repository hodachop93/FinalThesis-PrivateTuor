'use strict';

//function timeout(ms, promise) {
//    return new Promise(function (resolve, reject) {
//        promise
//            .then(resolve)
//            .catch(reject);
//        setTimeout(function () {
//            console.log('day la time out');
//            reject(new Error('Timeout after ' + ms + ' ms')); // (A)
//        }, ms);
//    });
//}
//
//timeout(5000, httpGet('http://exploringjs.com/es6/ch_promises.html'))
//    .then(function (value) {
//        console.log('Contents: ' + value);
//    })
//    .catch(function (reason) {
//        console.error('Error or timeout', reason);
//    });
//
//function httpGet(url) {
//    return new Promise(
//        function (resolve, reject) {
//            const request = new XMLHttpRequest();
//            request.onload = function () {
//                if (this.status === 200) {
//                    // Success
//                    resolve('Success');
//                } else {
//                    // Something went wrong (404 etc.)
//                    reject(new Error(this.statusText));
//                }
//            };
//            request.onerror = function () {
//                reject(new Error(
//                    'XMLHttpRequest Error: ' + this.statusText));
//            };
//            request.open('GET', url);
//            request.send();
//        });
//}
function asyncFunc() {
    return new Promise(function (resolve, reject) {
        resolve('Error'); // success
    });
}

asyncFunc().then(function (response) {
        console.log('then 1: ' + response);
        // asyncFunc();
    })
    .then(function (response) {
        console.log('then 2: ' + response);
    })
    .catch(function (err) {
        console.log('catch:' + err);
    });
/*class Point {
 constructor(x, y) {
 if(arguments.length){
 this.x = x;
 this.y = y;
 }else{
 this.x = 100;
 this.y = 200;
 }

 }
 toString() {
 return '(' + this.x + ', ' + this.y + ')';
 }
 }

 class ColorPoint extends Point {
 constructor(x, y, color) {
 super();
 this.color = color;
 }
 toString() {
 //return super.toString() + ' in ' + this.color;
 return this.x + ', ' + this.y;
 }
 }

 let cp = new ColorPoint(25, 8, 'green');
 console.log(cp.toString());*/
