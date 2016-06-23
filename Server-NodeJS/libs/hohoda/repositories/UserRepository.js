import * as objection from 'objection';
import BaseRepository from "./BaseRepository";
import User from "./../models/User";
import Utils from "./../util/Utils";

export default class UserRepository extends BaseRepository {
    constructor() {
        super();
    }

    createUser(user) {
        let self = this;
        user.created_at = Utils.generateTimestampLong();
        user.updated_at = Utils.generateTimestampLong();
        let profileName = user.name;
        delete user.name;
        return objection.transaction(User, function (User) {
            return User.query().insert(user)
                .then(function (userReturn) {
                    let profile = {
                        user_id: user.id,
                        name: profileName,
                        created_at: user.created_at,
                        updated_at: user.updated_at
                    };

                    return self.createProfile(user, userReturn, profile);
                })
        });
    }

    async loginSocialNetwork(userReq, request) {
        let _this = this;
        return objection.transaction(User, function (User) {
            return User.query().insert(userReq)
                .then(userReturn => {
                    let gender;
                    if (request.gender == 'male')
                        gender = 0;
                    else
                        gender = 1;

                    let profile = {
                        user_id: userReturn.id,
                        name: request.name,
                        gender: gender,
                        avatar_url: request.avatar_url,
                        created_at: userReturn.created_at,
                        updated_at: userReturn.updated_at
                    };
                    return _this.createProfile(userReq, userReturn, profile);
                })
                .catch(err => {
                    console.log(err);
                });
        });
    }

    createProfile(user, userReturn, profile) {
        return userReturn.$relatedQuery('hasProfile').insert(profile);
    }

    findUserByUserNameAndEmail(userName, email) {
        return User.query().where("user_name", userName).andWhere('email', email).first();
    }

    findUserByUserID(userID) {
        return User.query().where("id", userID).first();
    }

    updateUser(user) {
        let update = {
            status: user.status,
            updated_at: Utils.generateTimestampLong()
        }
        return User.query().patch(update).where('id', user.id);
    }

    findUserByUsernameOrEmail(accountID) {
        return User.query().where("user_name", accountID).orWhere("email", accountID).first();
    }
}