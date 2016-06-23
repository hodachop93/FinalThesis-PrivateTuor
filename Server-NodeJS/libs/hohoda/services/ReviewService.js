import BaseService from "./BaseService";
import UserException from "./../exceptions/UserException";
import ReviewRepository from "./../repositories/ReviewRepository";
import ProfileRepository from "./../repositories/ProfileRepository";
import {INTERNAL_SERVER_ERROR} from "./../util/ApplicationConstants";
import i18n from "i18n";

export default class ReviewService extends BaseService {
    constructor(reviewRepo) {
        super();
        if (arguments.length) {
            this._repository = reviewRepo;
        } else {
            this._repository = new ReviewRepository();
        }
    }

    async addNew(userId, reviewReq) {
        let profileRepo = new ProfileRepository();
        let review = await this._repository.addNew(userId, reviewReq);

        //update rate average
        let totalReview = parseFloat(await this._repository.getTotalReview(reviewReq.to_user_id));
        if (!totalReview){
            totalReview = 0;
        }
        let newRate = parseFloat(reviewReq.rate);

        let profile = await profileRepo.getProfile(reviewReq.to_user_id);
        let oldRateAverage;
        if (profile.rate_average) {
            oldRateAverage = parseFloat(profile.rate_average);
        } else {
            oldRateAverage = 0;
        }

        let newRateAverage = (oldRateAverage * totalReview + newRate) / totalReview;

        await profileRepo.updateRateAverage(reviewReq.to_user_id, newRateAverage);
        return review;
    }

    getAllReview(userId, timeNext) {
        return this._repository.getAllReview(userId, timeNext);
    }
}