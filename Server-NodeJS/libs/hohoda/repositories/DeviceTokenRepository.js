import BaseRepository from "./BaseRepository";
import DeviceToken from "./../models/DeviceToken";
import Utils from "./../util/Utils";

export default class DeviceTokenRepository extends BaseRepository {

    constructor() {
        super();
    }

    async saveDeviceToken(userId, deviceToken) {
        let req = {
            device_token: deviceToken,
            user_id: userId,
            created_at: Utils.generateTimestampLong()
        };
        let deviceTokenFound = await this.findDeviceToken(deviceToken);
        if (deviceTokenFound) {
            return await DeviceToken.query().patch(req).where('device_token', deviceToken);
        } else {
            return await DeviceToken.query().insert(req);
        }
    }

    removeDeviceToken(user_id, device_token) {
        return DeviceToken
            .query()
            .delete()
            .where("device_token", device_token)
            .andWhere("user_id", user_id);
    }

    /**
     * Get device token list of a user
     * @param userId
     */
    getDeviceTokens(userId) {
        return DeviceToken.query()
            .pluck('device_token')
            .where('user_id', userId);
    }

    async findDeviceToken(deviceToken) {
        let deviceTokenFound = await DeviceToken.query().where('device_token', deviceToken).first();
        return deviceTokenFound;
    }


}