import JWT from "jsonwebtoken";
import Container from "./../../libs/hohoda/core/Container";
import Logger from "./../../libs/hohoda/core/Logger";
import {TOKEN_ID_NOT_FOUND, USER_ID_NOT_FOUND, TOKEN_SECURE_KEY} from "./../../libs/hohoda/util/ApplicationConstants";
import i18n from "i18n";

export default class APIAuthMiddleware {

    handle (request, response, next) {
        let token = request.body.token || request.query.token || request.headers['x-access-token'];
        if (token) {
            // verifies secret and checks exp
            JWT.verify(token, TOKEN_SECURE_KEY, function (err, decoded) {
                if (err) {
                    Logger.info("Authenticate error: " + i18n.__('msg_error_token_id_150003') + " - Invalid provided.");
                    return response.status(403).send({
                        status: 1,
                        messageCode: TOKEN_ID_NOT_FOUND,
                        messageInfo: i18n.__('msg_error_token_id_150003'),
                        devInfo: 'Invalid provided.',
                        body: null
                    });
                } else {
                    let userID = request.headers['user_id'];
                    if(userID) {
                        let authenticateuserService = Container.getService("AuthenticateuserService");
                        authenticateuserService.findUserStatusByUserID(userID)
                            .then(userFound => {
                                // if everything is good, save to request for use in other routes
                                request.decoded = decoded;
                                next();
                            })
                            .catch(err => {
                                Logger.info("Authenticate error: " + err);
                                if(err.data) {
                                    response.json({
                                        status: 1,
                                        messageCode: err.errorCode,
                                        messageInfo: err.message,
                                        devInfo: err.errorDevMessage,
                                        body : {
                                            user_id : err.data.id
                                        }
                                    });
                                } else {
                                    response.json({
                                        status: 1,
                                        messageCode: err.errorCode,
                                        messageInfo: err.message,
                                        devInfo: err.errorDevMessage,
                                        body : null
                                    });
                                }
                            })
                    } else {
                        Logger.info("Authenticate error: " + i18n.__('msg_error_user_id_150004'));
                        response.json({
                            status: 1,
                            messageCode: USER_ID_NOT_FOUND,
                            messageInfo: i18n.__('msg_error_user_id_150004'),
                            devInfo: i18n.__('msg_error_user_id_150004'),
                            body : null
                        });
                    }
                }
            });

        } else {
            Logger.info("Authenticate error: " + i18n.__('msg_error_token_id_150003') + " - No token provided.");
            // if there is no token
            // return an error
            return response.status(403).send({
                status: 1,
                messageCode: TOKEN_ID_NOT_FOUND,
                messageInfo: i18n.__('msg_error_token_id_150003'),
                devInfo: 'No token provided.',
                body: null
            });
        }
    };

}
