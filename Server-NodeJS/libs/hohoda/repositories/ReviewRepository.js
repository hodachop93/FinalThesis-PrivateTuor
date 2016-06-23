import BaseRepository from "./BaseRepository";
import Review from "./../models/Review";
import Profile from "./../models/Profile";
import Utils from './../util/Utils';
import {MAX_ITEMS_PER_LOAD} from "./../util/ApplicationConstants";

export default class ReviewRepository extends BaseRepository {

    constructor() {
        super();
    }

    addNew(userId, reviewReq) {
        let review = {
            id: Utils.generateUUID(),
            reviewer_id: userId,
            to_user_id: reviewReq.to_user_id,
            rate: reviewReq.rate,
            comment: reviewReq.comment,
            created_at: Utils.generateTimestampLong(),
            updated_at: Utils.generateTimestampLong()
        };

        return Review.query().insert(review);
    }

    getAllReview(userId, timeNext) {
        return Review.query()
            .eager('[reviewer]')
            .pick(Profile, ['name', 'avatar_url'])
            .where('to_user_id', userId)
            .andWhere('created_at', '<', timeNext)
            .orderBy('created_at', 'desc')
            .limit(MAX_ITEMS_PER_LOAD);
    }

    getTotalReview(userId){
        return Review.query()
            .count('* as count')
            .pluck('count')
            .where('to_user_id', userId)
            .first();
    }
}