import UserService from "./UserService";
import UserRepository from "./../repositories/UserRepository";

export default class AuthenticateuserService extends UserService {

    constructor(userRepository) {
        super();
        if (arguments.length) {
            this._repository = userRepository;
        } else {
            this._repository = new UserRepository();
        }
    }

    findUserStatusByUserID(userID) {
        return this._repository.findUserByUserID(userID)
            .then(userFound => {
                if (userFound == null) {
                    this.buildUserResponseIfNotFound();
                } else {
                    if (userFound.status == 1) {
                        return userFound;
                    } else {
                        this.buildUserResponseWhenAuthenticate(userFound);
                    }
                }
            })
    }

    buildUserResponseWhenAuthenticate(userFound) {
        if (userFound.status == 0) { // In-Activated
            this.buildUserResponseIfInActivated(userFound);
        } else if (userFound.status == 2) { // Blocked
            this.buildUserResponseIfBlocked(userFound);
        } else if (userFound.status == 3) { // Destroyed
            this.buildUserResponseIfDestroyed(userFound);
        }
    }

}