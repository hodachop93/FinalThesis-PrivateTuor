import * as objection from 'objection';
import BaseRepository from "./BaseRepository";
import UserHashtagRepository from './UserHashtagRepository';
import Profile from "./../models/Profile";
import User from "./../models/User";
import Review from "./../models/Review";
import NotificationDetail from "./../models/NotificationDetail";
import Utils from "./../util/Utils";
import {NOTIFICATION_STATUS_UNREAD, NOTIFICATION_STATUS_READ, MAX_ITEMS_PER_LOAD} from "./../util/ApplicationConstants";


export default class ProfileRepository extends BaseRepository {

    constructor() {
        super();
    }

    /**
     * Get a user id
     * @param userId A string user id
     * @returns a profile
     */
    async getProfile(userId) {
        let profile = await Profile.query()
            .eager('[interested_hashtags]')
            .select('profile.*', 'user.user_name', 'user.email', 'user.account_type')
            .join('user', 'profile.user_id', 'user.id')
            .where('profile.user_id', userId)
            .first();

        let total_review = await Review.query()
            .count('* as count')
            .where('to_user_id', userId)
            .first();
        let total_unread_notification = await NotificationDetail.query()
            .count('* as count')
            .where('to_user_id', userId)
            .andWhere('status', NOTIFICATION_STATUS_UNREAD)
            .first();

        profile.total_review = total_review.count;
        profile.total_unread_notification = total_unread_notification.count;
        return profile;
    }

    /**
     * Get a profile list
     * @param userIdList A user id list
     * @param timeNext
     */
    getProfileList(userIdList, timeNext) {
        return Profile.query()
            .where('user_id', 'in', userIdList)
            .andWhere('created_at', '<', timeNext)
            .groupBy('user_id')
            .orderBy('created_at', 'desc')
            .limit(MAX_ITEMS_PER_LOAD);
    }

    getAllTutorProfile(timeNext, userId) {
        return Profile.query()
            .where('created_at', '<', timeNext)
            .whereNot('user_id', userId)
            .groupBy('created_at')
            .orderBy('created_at', 'desc')
            .limit(MAX_ITEMS_PER_LOAD);
    }

    getLatLngUserAddress(userId) {
        return Profile.query()
            .select('lat', 'lng')
            .where('user_id', userId)
            .first();
    }

    async updateUserProfile(updateProfileReq, avatarUrl) {
        let requestBody = updateProfileReq.requestBody;
        let update = {
            name: requestBody.name,
            phone: requestBody.phone,
            address: requestBody.address,
            infor: requestBody.infor,
            age: requestBody.age,
            gender: requestBody.gender,
            updated_at: Utils.generateTimestampLong()
        };
        if (requestBody.lat) {
            update.lat = requestBody.lat;
            update.lng = requestBody.lng;
        }

        if (avatarUrl) {
            update.avatar_url = avatarUrl;
        }

        let interestedSubjects = requestBody.interested_subject;
        let userId = updateProfileReq.userId;
        let userHashtagRepo = new UserHashtagRepository();

        await userHashtagRepo.updateUserInterestedSubjects(userId, interestedSubjects);

        return Profile.query().patch(update).where('user_id', updateProfileReq.userId);
    }

    searchTutorByName(userId, keys, timeNext) {
        let listSearch = '%';
        if (Utils.isString(keys)) {
            listSearch += keys;
        } else {
            listSearch += keys[0];
            for (let i = 1; i < keys.length; i++) {
                listSearch += ' ' + keys[i];
            }
        }
        listSearch += '%';

        return Profile.query()
            .where('created_at', '<', timeNext)
            .whereNot('user_id', userId)
            .where('name', 'like', listSearch)
            .groupBy('created_at')
            .orderBy('created_at', 'desc')
            .limit(MAX_ITEMS_PER_LOAD);
    }

    updateAllProfile(userId, updateProfileReq, AVATAR_URL) {
        let _this = this;
        return objection.transaction(User, Profile, function (User, Profile) {
            if (!updateProfileReq.new_password) {
                return _this.updateDetailAndAvatar(userId, updateProfileReq, AVATAR_URL, Profile);
            } else {
                let update = {
                    password: Utils.hashPassword(updateProfileReq.new_password),
                    updated_at: Utils.generateTimestampLong()
                }
                return User.query().patch(update).where('id', userId)
                    .then(() => {
                        return _this.updateDetailAndAvatar(userId, updateProfileReq, AVATAR_URL, Profile);
                    });
            }
        });
    }

    updateDetailAndAvatar(userId, updateProfileReq, AVATAR_URL, Profile) {
        if (!AVATAR_URL) {
            let update = {
                name: updateProfileReq.name,
                email: updateProfileReq.email,
                address: updateProfileReq.address,
                infor: updateProfileReq.infor,
                updated_at: Utils.generateTimestampLong()
            }
            return Profile.query().patch(update).where('user_id', userId);
        } else {
            let update = {
                name: updateProfileReq.name,
                email: updateProfileReq.email,
                address: updateProfileReq.address,
                infor: updateProfileReq.infor,
                avatar_url: AVATAR_URL,
                updated_at: Utils.generateTimestampLong()
            }
            return Profile.query().patch(update).where('user_id', userId);
        }
    }

    updateRateAverage(userId, newRateAverage) {
        return Profile.query()
            .patch({rate_average: newRateAverage})
            .where('user_id', userId);
    }
}