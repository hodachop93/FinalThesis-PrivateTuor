import BaseController from "./../../libs/hohoda/core/framework/BaseController";
import Container from "./../../libs/hohoda/core/Container";
import i18n from 'i18n';
import Utils from './../../libs/hohoda/util/Utils';
import {EMAIL_SERVER_ERROR} from "./../../libs/hohoda/util/ApplicationConstants";

export default class RegistrationuserController extends BaseController {

    constructor(req, res) {
        super(req, res);
    }

    register() {
        let userReg = this.request.body;
        let registrationUserService = Container.getService("RegistrationuserService");

        registrationUserService.registerUser(userReg)
            .then(settingReturn => {
                let message = i18n.__('msg_text_your_activation_code') + Utils.generatorOTP(settingReturn.user_id);
                let to = userReg.email;

                let emailService = Container.getService('EmailService');
                emailService.sendEmail(to, message)
                    .then(() => {
                        this.response.json({
                            status: 0,
                            messageCode: 200,
                            messageInfo: null,
                            body: {
                                user_id: settingReturn.user_id
                            }
                        });
                    })
                    .catch(error => {
                        this.response.json({
                            status: 1,
                            messageCode: SMS_SERVER_ERROR,
                            messageInfo: i18n.__('msg_error_email_150002'),
                            devInfo: error.message
                        })
                    });
            })
            .catch(err => {
                this.handleErrorException(err);
            });
    }
}