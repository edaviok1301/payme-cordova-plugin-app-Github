var exec = require('cordova/exec');

exports.initPayme = function (arg0, success, error) {
    exec(success, error, 'SdkPaymev2', 'initPayme', [arg0]);
};

