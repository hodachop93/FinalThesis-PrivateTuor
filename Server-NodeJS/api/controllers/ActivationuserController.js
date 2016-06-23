import BaseController from "./../../libs/hohoda/core/framework/BaseController";
import Container from "./../../libs/hohoda/core/Container";
import JWT from 'jsonwebtoken';
import {TOKEN_SECURE_KEY, TOKEN_EXPIRE_DATE} from "./../../libs/hohoda/util/ApplicationConstants";


export default class ActivationuserController extends BaseController {

    constructor(req, res) {
        super(req, res);
    }

    activateUser() {
        let otpReq = this.request.body;

        let activeUserService = Container.getService('ActiveuserService');
        activeUserService.activateUserByOTPCode(otpReq)
            .then(user => {
                let token = JWT.sign(user, TOKEN_SECURE_KEY, {
                    expiresIn: TOKEN_EXPIRE_DATE
                });

                // return the information including token as JSON
                this.response.json({
                    status: 0,
                    messageCode: 200,
                    messageInfo: null,
                    body: {
                        user_id: user.id,
                        token_id: token
                    }
                });
            })
            .catch(err => {
                this.handleErrorException(err);
            });
    }
}
