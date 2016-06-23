import UserService from "./UserService";
import Utils from "./../util/Utils";
import {
    USER_STATUS_NOT_ACTIVATED,
    USER_STATUS_ACTIVATED,
    USER_STATUS_BLOCKED,
    USER_STATUS_DESTROYED
} from "./../util/ApplicationConstants";

export default class SigninuserService extends UserService {

    constructor() {
        super();
    }

    async signinUserByUsernameOrEmail(signinRequest) {
        let user = await this._repository.findUserByUsernameOrEmail(signinRequest.account_id);
        if (user) {
            if (Utils.validatePassword(signinRequest.password, user.password)) {
                if (user.status == USER_STATUS_ACTIVATED) {
                    return user;
                } else {
                    this.buildUserResponseWhenSignIn(user);
                }
            }
        } else {
            this.buildUserResponseIfNotFound();
        }

    }

    async loginSocialNetwork(request) {
        //use social_id to username
        let user = await this._repository.findUserByUsernameOrEmail(request.social_id);
        if (!user)
            user = await this._repository.findUserByUsernameOrEmail(request.email);
        if (!user) {
            let uuid = Utils.generateUUID();
            let userReq = this.buildUserRequest(request, uuid);
            console.log(userReq);
            let userResult = await this._repository.loginSocialNetwork(userReq, request);
            return userResult;
        } else {
            if (user.status == 1) {
                return user;
            } else {
                this.buildWalletResponseWhenLoginSocial(user);
            }
        }
    }

    buildUserRequest(request, uuid) {
        let user = {
            id: uuid,
            user_name: request.social_id,
            account_type: request.account_type,
            email: request.email,
            created_at: Utils.generateTimestampLong(),
            updated_at: Utils.generateTimestampLong(),
            status: USER_STATUS_ACTIVATED // Auto set status is activated
        }
        return user;
    }

    buildWalletResponseWhenLoginSocial(user) {
        if (user.status == 2) { // Blocked
            this.buildUserResponseIfBlocked(user);
        } else if (user.status == 3) { // Destroyed
            this.buildUserResponseIfDestroyed(user);
        }
    }

    buildUserResponseWhenSignIn(userFound) {
        if (userFound.status == USER_STATUS_NOT_ACTIVATED) { // In-Activated
            this.buildUserResponseIfInActivated(userFound);
        } else if (userFound.status == USER_STATUS_BLOCKED) { // Blocked
            this.buildUserResponseIfBlocked(userFound);
        } else if (userFound.status == USER_STATUS_DESTROYED) { // Destroyed
            this.buildUserResponseIfDestroyed(userFound);
        }
    }
}