import BaseController from "./../../libs/hohoda/core/framework/BaseController";
import Container from "./../../libs/hohoda/core/Container";

export default class SignoutController extends BaseController {

    async signOut() {
        let userId = this.request.headers['user_id'];
        let deviceToken = this.request.headers['devicetokenid'];

        let signoutService = Container.getService("SignoutService");
        try{
            await signoutService.signOut(userId, deviceToken);
            this.response.json({
                status: 0,
                messageCode: 200,
                messageInfo: null,
                body: null
            });
        }catch(err){
            this.handleErrorException(err);
        }
    }
}