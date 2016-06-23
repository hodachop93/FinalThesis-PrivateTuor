import BaseService from "./BaseService";
import ProfileRepository from "./../repositories/ProfileRepository";
import UserException from "./../exceptions/UserException";
import i18n from "i18n";
import {CAN_NOT_FETCH_PROFILE, CAN_NOT_UPDATE_PROFILE} from "./../util/ApplicationConstants";
import path from "path";
import fsExtra from "fs-extra";
import base64 from "node-base64-image";
import validator from "validator";

export default class UserprofileService extends BaseService {

    constructor(profileRepository) {
        super()
        if (arguments.length) {
            this._repository = profileRepository;
        } else {
            this._repository = new ProfileRepository();
        }
    }

    getUserProfile(userId) {
        return this._repository.getProfile(userId)
            .then(profile => {
                return profile;
            })
            .catch(err => {
                throw new UserException(i18n.__('msg_error_profile_40002'),
                    CAN_NOT_FETCH_PROFILE, err.message);
            })
    }

    /**
     * Update user's profile
     * @param updateProfileReq
     * @returns {*} return new profile if success
     */
    async updateUserProfile(updateProfileReq) {
        let imageStr = updateProfileReq.requestBody.image_string;

        try {
            let avatarUrl;
            if (imageStr != null) {
                avatarUrl = await this.uploadAvatar(updateProfileReq);
            }

            let newProfile = await this._repository.updateUserProfile(updateProfileReq, avatarUrl);
            return newProfile;
        } catch (err) {
            throw new UserException(i18n.__("msg_error_profile_40001"), CAN_NOT_UPDATE_PROFILE, err.message);
        }
    }

    /**
     * Upload avatar
     * @param updateProfileReq update profile request
     * @returns {string} Avatar url if success
     */
    async uploadAvatar(updateProfileReq) {
        let image_buffer = null;
        let imageStr = updateProfileReq.requestBody.image_string;
        if (validator.isBase64(imageStr)) {
            image_buffer = new Buffer(imageStr, "base64");
        } else {
            throw new UserException(i18n.__("msg_error_profile_40001"), CAN_NOT_UPDATE_PROFILE, "Invalid image format (not base64)");
        }

        let upload_time = new Date().getTime();
        let fileName = "avatar_" + upload_time;
        let folderPath = path.join("uploads", updateProfileReq.userId);
        let targetPath = path.join("uploads", updateProfileReq.userId, fileName);

        try {
            await fsExtra.ensureDir(folderPath);
            let options = {
                filename: targetPath
            };
            await base64.base64decoder(image_buffer, options, function (err, result) {
            });
            let avatarUrl = updateProfileReq.apiHost + "/photos/" + updateProfileReq.userId + "/" + fileName + ".jpg";
            return avatarUrl;
        } catch (err) {
            throw new UserException(i18n.__("msg_error_profile_40001"), CAN_NOT_UPDATE_PROFILE, err.message);
        }

    }

}