import UserService from './UserService';
import UserRepository from './../repositories/UserRepository';
import Utils from './../util/Utils';
import UserException from './../exceptions/UserException';
import i18n from 'i18n';
import {THE_USER_NAME_OR_EMAIL_HAS_ALREADY_USED} from './../util/ApplicationConstants';

export default class RegistrationuserService extends UserService {

    constructor() {
        super();
    }

    registerUser(userReg) {
        return this.checkUserHasAlreadyToUsed(userReg)
            .then(userFound => {
                if (!userFound) {
                    let uuid = Utils.generateUUID();
                    let userReq = this.buildUserRequest(userReg, uuid);

                    return this._repository.createUser(userReq)
                        .then(settingReturn => {
                            return settingReturn;
                        })
                        .catch(err => {
                            this.buildUserResponseIfUserNameOrEmailHasAlreadyUsed(err);
                        })
                } else {
                    this.buildUserResponseWhenRegistered(userFound);
                }
            });
    }


    /*
     * Create a user request from user registration information and UUID
     */
    buildUserRequest(userReg, uuid) {
        let hashPassword = Utils.hashPassword(userReg.password);
        userReg.password = hashPassword;
        userReg.id = uuid;
        userReg.status = 0;
        return userReg;
    }

    buildUserResponseIfUserNameOrEmailHasAlreadyUsed(err) {
        throw new UserException(i18n.__('msg_error_user_30006'),
            THE_USER_NAME_OR_EMAIL_HAS_ALREADY_USED, err.message, null);
    }

    buildUserResponseWhenRegistered(userFound) {
        if (userFound.status == 0) { // In-Activated
            this.buildUserResponseIfInActivated(userFound);
        } else if (userFound.status == 1) { // Activated
            this.buildUserResponseIfActivated(userFound);
        } else if (userFound.status == 2) { // Blocked
            this.buildUserResponseIfBlocked(userFound);
        } else if (userFound.status == 3) { // Destroyed
            this.buildUserResponseIfDestroyed(userFound);
        }
    }
}