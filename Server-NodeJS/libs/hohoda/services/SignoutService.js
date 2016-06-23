import BaseService from "./BaseService";
import i18n from "i18n";
import UserException from "./../exceptions/UserException";
import DeviceTokenRepository from "./../repositories/DeviceTokenRepository";
import {INTERNAL_SERVER_ERROR} from "./../util/ApplicationConstants";

export default class SignoutService extends BaseService {

    constructor(deviceTokenRepo) {
        super();
        if (arguments.length) {
            this._repository = deviceTokenRepo;
        } else {
            this._repository = new DeviceTokenRepository();
        }
    }

    signOut(user_id, device_token) {
        return this._repository.removeDeviceToken(user_id, device_token)
            .catch(error=> {
                throw new UserException(i18n.__('msg_error_system_150001'), INTERNAL_SERVER_ERROR, error.message, null);
            });
    }
}