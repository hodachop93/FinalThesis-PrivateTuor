'use strict';

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

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
/*
 function asyncFunc() {
 return new Promise(
 function (resolve, reject) {
 reject('Error'); // success
 });
 }

 asyncFunc()
 .then(response => {
 console.log('then 1: ' + response);
 })
 .catch(err => {
 //return err;
 return Promise.reject(err);
 })
 .then(response => {
 console.log('response: '+ response);
 })
 .catch(err => {
 console.log('catch:' + err);
 })
 ;*/

var Point = function () {
    function Point(x, y) {
        _classCallCheck(this, Point);

        if (arguments.length) {
            this.x = x;
            this.y = y;
        } else {
            this.x = 100;
            this.y = 200;
        }
    }

    _createClass(Point, [{
        key: 'toString',
        value: function toString() {
            return '(' + this.x + ', ' + this.y + ')';
        }
    }]);

    return Point;
}();

var ColorPoint = function (_Point) {
    _inherits(ColorPoint, _Point);

    function ColorPoint(x, y, color) {
        _classCallCheck(this, ColorPoint);

        var _this = _possibleConstructorReturn(this, Object.getPrototypeOf(ColorPoint).call(this));

        _this.color = color;
        return _this;
    }

    _createClass(ColorPoint, [{
        key: 'toString',
        value: function toString() {
            //return super.toString() + ' in ' + this.color;
            return this.x + ', ' + this.y;
        }
    }]);

    return ColorPoint;
}(Point);

var cp = new ColorPoint(25, 8, 'green');
console.log(cp.toString());