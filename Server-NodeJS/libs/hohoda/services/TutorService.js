import BaseService from "./BaseService";
import ProfileRepository from './../repositories/ProfileRepository';
import UserHashtagRepository from './../repositories/UserHashtagRepository';
import HashtagRepository from './../repositories/HashtagRepository';
import UserException from "./../exceptions/UserException";
import {INTERNAL_SERVER_ERROR} from "./../util/ApplicationConstants";
import i18n from "i18n";
import geolib from "geolib";
import request from 'request';
import config from './../../../configs/config';
import async from "async";

export default class TutorService extends BaseService {

    constructor(profileRepository) {
        super();
        if (arguments.length) {
            this._repository = profileRepository;
        } else {
            this._repository = new ProfileRepository();
        }
    }

    async getTutorByUserInterest(userId, reqBody) {
        //get hashtags of userid
        let userHashtagRepo = new UserHashtagRepository();
        let hashtagsIds = await userHashtagRepo.getHashtagListByUserId(userId);

        //get user id list have at least one above hashtag
        let userIds = await userHashtagRepo.getUserIdListHaveHashtagList(userId, hashtagsIds);

        //get profile of these user id list
        let timeNext = reqBody.time_next;
        let profileList = await this._repository.getProfileList(userIds, timeNext);
        return profileList;
    }

    async getTutorByUserLocal(userId, reqBody) {
        let timeNext = reqBody.time_next;
        try {
            // get user's lat, lng
            let profileRepo = new ProfileRepository();
            let latLng = await profileRepo.getLatLngUserAddress(userId);

            let req = {
                lat: latLng.lat,
                lng: latLng.lng,
                radius: 50000, // 50 km
                time_next: timeNext
            };
            return await this.searchTutorByLocation(req, userId);
        } catch (err) {
            throw new UserException(i18n.__('msg_error_system_150001'), INTERNAL_SERVER_ERROR, err.message);
        }
    }

    async searchTutorByLocation(request, userId) {
        let lat = request.lat;
        let lng = request.lng;
        let radius = request.radius;
        let timeNext = request.time_next;

        let tutors = await this._repository.getAllTutorProfile(timeNext, userId);
        let validInsideTutors = [];
        tutors.forEach(item => {
            if (!item.lat && !item.lng) {
                return;
            }
            if (this.isPointInCircle(item.lat, item.lng, lat, lng, radius)) {
                validInsideTutors.push(item);
            }
        });
        return validInsideTutors;
    }

    async searchTutorsByHashtag(userId, hashtags, timeNext) {
        let hashtagRepo = new HashtagRepository();
        let userHashtagRepo = new UserHashtagRepository();

        let hashtagIds = await hashtagRepo.getHashtagListByName(hashtags);
        let userIds = await userHashtagRepo.getUserIdListHaveHashtagList(userId, hashtagIds);
        let tutors = await this._repository.getProfileList(userIds, timeNext);
        let appendTutors = await this.searchTutorByName(userId, hashtags, timeNext);
        for (let item of appendTutors){
            tutors.push(item);
        }
        return tutors;
    }

    searchTutorByName(userId, hashtags, timeNext){
        return this._repository.searchTutorByName(userId, hashtags, timeNext);
    }

    isPointInCircle(lat, lng, latCenter, lngCenter, radius) {

        return geolib.isPointInCircle(
            {latitude: lat, longitude: lng},
            {latitude: latCenter, longitude: lngCenter},
            radius
        );
    }
}