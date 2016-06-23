import BaseController from "./../../libs/hohoda/core/framework/BaseController";
import Container from "./../../libs/hohoda/core/Container";
import JWT from "jsonwebtoken";
import {TOKEN_SECURE_KEY, TOKEN_EXPIRE_DATE} from "./../../libs/hohoda/util/ApplicationConstants";
import Hashtag from "./../../libs/hohoda/models/Hashtag";
import Utils from './../../libs/hohoda/util/Utils';
export default class SigninController extends BaseController {

    constructor(req, res) {
        super(req, res);
    }

    signin() {
        let signinReq = this.request.body;
        let signinUserService = Container.getService('SigninuserService');
        signinUserService.signinUserByUsernameOrEmail(signinReq)
            .then(user => {
                if (!user){
                    signinUserService.buildUserResponseIfNotFound();
                }
                let token = JWT.sign(user, TOKEN_SECURE_KEY, {
                    expiresIn: TOKEN_EXPIRE_DATE
                });

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
            .catch(error => {
                this.handleErrorException(error);
            });
    }

    loginSocialNetwork(){
        let loginSocialNetworkReq = this.request.body;
        let signinUserService = Container.getService('SigninuserService');
        signinUserService.loginSocialNetwork(loginSocialNetworkReq)
            .then(user => {
                if (!user){
                    signinUserService.buildUserResponseIfNotFound();
                }
                let token = JWT.sign(user, TOKEN_SECURE_KEY, {
                    expiresIn: TOKEN_EXPIRE_DATE
                });
                let userId;
                if (user.id){
                    userId = user.id
                }else{
                    userId = user.user_id;
                }
                this.response.json({
                    status: 0,
                    messageCode: 200,
                    messageInfo: null,
                    body: {
                        user_id: userId,
                        token_id: token
                    }
                });
            })
            .catch(error => {
                this.handleErrorException(error);
            });

    }
}