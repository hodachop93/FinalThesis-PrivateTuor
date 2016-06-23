import BaseService from './BaseService';
import nodemailer from 'nodemailer';


let transporter = nodemailer.createTransport('smtps://hodachop93%40gmail.com:nhocpro1321993@smtp.gmail.com');

const COMPANY_EMAIL = 'Hohoda Application <hohodaapp@gmail.com>';
const SUBJECT_EMAIL = 'Please verify Hohoda account';

export default class EmailService extends BaseService {

    constructor() {
        super();
    }

    sendEmail(to, message) {
        let mailOptions = {
            from: COMPANY_EMAIL, // sender address
            to: to, // list of receivers
            subject: SUBJECT_EMAIL, // Subject line
            text: message, // plaintext body
            html: message // html body
        };

        return new Promise(function (resolve, reject) {
            transporter.sendMail(mailOptions, function(error, info){
                if(error){
                    reject(error);
                }
                resolve(info);
            });
        });
    }
}