import uuid from 'node-uuid';
import Bcrypt from 'bcrypt-nodejs';
import Speakeasy from 'speakeasy';
import PasswordMaker from 'password-maker';
import crypto from 'crypto';
import {OTP_EXPIRE_DATE} from "./ApplicationConstants";

export default class Utils {

    constructor() {

    }

    /*
     * Generate a uuid
     */
    static generateUUID() {
        return uuid.v1({
            node: [0x01, 0x23, 0x45, 0x67, 0x89, 0xab],
            clockseq: 0x1234,
            nsecs: 5678
        });
    }

    static hashPassword(password) {
        return Bcrypt.hashSync(password, Bcrypt.genSaltSync());
    }

    static validatePassword(password, actualPassword) {
        return Bcrypt.compareSync(password, actualPassword);
    }

    static generatorOTP(key) {
        //step default is 30 seconds
        return Speakeasy.totp({secret: key, step: OTP_EXPIRE_DATE});
    }


    static validateOTP(otpCode, key) {
        let validOTP = Speakeasy.totp({secret: key, step: OTP_EXPIRE_DATE});
        if (otpCode === validOTP) {
            return true;
        } else {
            return false;
        }
    }

    static randomPassWord() {
        var options = {
            uppercase: true,
            symbols: false,
            numbers: true
        };
        return PasswordMaker(options, 8);
    }

    static encodeBase64(text) {
        var buffer = new Buffer(text);
        return buffer.toString('base64');
    }

    static decodeBase64(toBase64) {
        var buffer = new Buffer(toBase64, 'base64');
        return buffer.toString('utf8');
    }

    static cryptoSHA256(text) {
        return crypto.createHash('sha256').update(text).digest("hex");
    }

    static generateTimestampLong() {
        return new Date().getTime();
    }

    static isString(s) {
        return s.constructor === String;
    }

}