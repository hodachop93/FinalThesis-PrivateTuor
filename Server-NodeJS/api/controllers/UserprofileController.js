import BaseController from "./../../libs/hohoda/core/framework/BaseController";
import Container from "./../../libs/hohoda/core/Container";


export default class UserprofileController extends BaseController {

    getUserProfile() {
        let userId = this.request.body.user_id;

        let userProfileService = Container.getService("UserprofileService");
        userProfileService.getUserProfile(userId)
            .then(profile => {
                this.response.json({
                    status: 0,
                    messageCode: 200,
                    messageInfo: null,
                    body: profile
                })
            })
            .catch(err => {
                this.handleErrorException(err);
            });
    }

    updateUserProfile() {
        let userId = this.request.headers['user_id'];
        const API_HOST = this.request.protocol + "://" + this.request.hostname
            + ":" + this.request.app.get("port");
        let reqBody = this.request.body;

        let updateProfileReq = {
            userId: userId,
            apiHost: API_HOST,
            requestBody: reqBody
        };
        let userProfileService = Container.getService("UserprofileService");
        userProfileService.updateUserProfile(updateProfileReq)
            .then(() => {
                this.response.json({
                    status: 0,
                    messageCode: 200,
                    messageInfo: null,
                    body: null
                })
            })
            .catch(err => {
                this.handleErrorException(err);
            });
    }
}