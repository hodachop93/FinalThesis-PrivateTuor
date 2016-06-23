import UserService from "./UserService";
import Utils from "./../util/Utils";
import {USER_CANT_NOT_ACTIVATE_BY_OTP_CODE} from "./../util/ApplicationConstants";

export default class ActiveuserService extends UserService {

    constructor() {
        super();
    }

    activateUserByOTPCode(otpRequest) {
        return this._repository.findUserByUserID(otpRequest.user_id)
            .then(userFound => {
                if (userFound == null) {
                    this.buildUserResponseIfNotFound();
                } else {
                    if (userFound.status == 0) { // In-Activated
                        let isValidOTP = Utils.validateOTP(otpRequest.otp, otpRequest.user_id);
                        if (isValidOTP) {
                            return this.updateUserStatus(otpRequest)
                                .then(() => {
                                    return userFound;
                                });
                        } else {
                            this.buildUserResponseIfInvalidOTPCode();
                        }
                    }
                    else {
                        this.buildUserResponseWhenResendOTPOrActivate(userFound);
                    }
                }
            });
    }

    buildUserResponseWhenResendOTPOrActivate(userFound) {
        if (userFound.status == 1) { // Activated
            this.buildUserResponseIfActivated(userFound);
        } else if (userFound.status == 2) { // Blocked
            this.buildUserResponseIfBlocked(userFound);
        } else if (userFound.status == 3) { // Destroyed
            this.buildUserResponseIfDestroyed(userFound);
        }
    }
}