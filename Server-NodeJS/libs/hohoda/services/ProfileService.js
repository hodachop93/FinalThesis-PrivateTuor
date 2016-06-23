import BaseService from "./BaseService";
import UserException from "./../exceptions/UserException";
import ProfileRepository from "./../repositories/ProfileRepository";
import HashtagRepository from './../repositories/HashtagRepository';
import {INTERNAL_SERVER_ERROR} from "./../util/ApplicationConstants";
import i18n from "i18n";

export default class ProfileService extends BaseService {

    constructor(profileRepository) {
        super();
        if (arguments.length) {
            this._repository = profileRepository;
        } else {
            this._repository = new ProfileRepository();
        }
    }

    getProfile(userId){
        return this._repository.getProfile(userId);
    }
}