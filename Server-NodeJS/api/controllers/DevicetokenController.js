import BaseController from "./../../libs/hohoda/core/framework/BaseController";
import Container from "./../../libs/hohoda/core/Container";

export default class DeviceTokenController extends BaseController{

    constructor(req, res) {
        super(req, res);
    }

    saveDeviceToken(){
        let userId = this.request.headers['user_id'];
        let deviceToken = this.request.headers['devicetokenid'];

        let deviceTokenService = Container.getService("DevicetokenService");
        deviceTokenService.saveDeviceToken(userId, deviceToken)
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