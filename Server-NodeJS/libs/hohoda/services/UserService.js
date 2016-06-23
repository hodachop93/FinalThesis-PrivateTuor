import BaseService from "./BaseService";
import UserRepository from "./../repositories/UserRepository";
import UserException from "./../exceptions/UserException";
import i18n from "i18n";
import {USER_EXISTED, USER_NOT_ACTIVATE, USER_BLOCKED, USER_DESTROYED, USER_NOT_FOUND,
    CAN_NOT_FETCH_PHONE_NUMBER, CAN_NOT_FETCH_USERNAME} from "./../util/ApplicationConstants";

export default class UserService extends BaseService{

    constructor(userRepository) {
        super();
        if (arguments.length) {
            this._repository = userRepository;
        } else {
            this._repository = new UserRepository();
        }
    }

    checkUserHasAlreadyToUsed(userReq) {
        return this._repository.findUserByUserNameAndEmail(userReq.user_name, userReq.email)
            .then(user => {
                return user; //same as Promise.resolve(user)
            })
            .catch(function (err) {
                return err; //same as Promise.resolve(err);
            });
    }



    buildUserResponseIfInActivated(userFound) {
        throw new UserException(i18n.__('msg_error_user_30002'),
            USER_NOT_ACTIVATE, i18n.__('msg_error_user_30002'), userFound);
    }

    buildUserResponseIfActivated(userFound) {
        throw new UserException(i18n.__('msg_error_user_30001'),
            USER_EXISTED, i18n.__('msg_error_user_30001'), userFound);
    }

    buildUserResponseIfBlocked(userFound) {
        throw new UserException(i18n.__('msg_error_user_30003'),
            USER_BLOCKED, i18n.__('msg_error_user_30003'), userFound);
    }

    buildUserResponseIfDestroyed(userFound) {
        throw new UserException(i18n.__('msg_error_user_30004'),
            USER_DESTROYED, i18n.__('msg_error_user_30004'), userFound);
    }

    buildUserResponseIfNotFound() {
        throw new UserException(i18n.__('msg_error_user_30005'),
            USER_NOT_FOUND, i18n.__('msg_error_user_30005'), null);
    }

    buildUserResponseIfInvalidOTPCode() {
        throw new UserException(i18n.__('msg_error_user_30009'),
            USER_CANT_NOT_ACTIVATE_BY_OTP_CODE, i18n.__('msg_error_user_30009'), null);
    }

    updateUserStatus(user) {
        user.status = 1;
        return this._repository.updateUser(user);
    }
}