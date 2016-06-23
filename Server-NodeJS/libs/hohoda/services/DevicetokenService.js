import BaseService from "./BaseService";
import UserException from "./../exceptions/UserException";
import DeviceTokenRepository from './../repositories/DeviceTokenRepository';
import i18n from "i18n";
import {INTERNAL_SERVER_ERROR} from "./../util/ApplicationConstants";

export default class DevicetokenService extends BaseService{

    constructor(deviceTokenRepo) {
        super();
        if(arguments.length){
            this._repository = deviceTokenRepo
        }else{
            this._repository = new DeviceTokenRepository();
        }
    }

    async saveDeviceToken(userId, deviceToken){
        return this._repository.saveDeviceToken(userId, deviceToken);
    }

    getDeviceTokens(userId){
        return this._repository.getDeviceTokens(userId);
    }
}