import * as objection from 'objection';
import BaseRepository from "./BaseRepository";
import UserHashtag from "./../models/UserHashtag";
import Popularhashtag from "./../models/Popularhashtag";
import Hashtag from "./../models/Hashtag";

export default class RecommendedjobsRepository extends BaseRepository {

    constructor() {
        super();
    }

    getHashtagIdsOfUser(userId) {
        return UserHashtag.query()
            .pluck('hashtag_id')
            .where('user_id', userId);
    }

    getPopularHashtags() {
        return Popularhashtag.query()
            .select("hashtag.name")
            .join("hashtag", "popular_hashtag.id", "hashtag.id");
    }

    getHashtagsByHashtagIds(hashtagids) {
        return Hashtag.query()
            .select('hashtag.name')
            .where('id', 'in', hashtagids);
    }

}