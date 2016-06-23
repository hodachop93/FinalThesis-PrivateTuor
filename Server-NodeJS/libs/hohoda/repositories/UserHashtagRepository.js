import BaseRepository from './BaseRepository';
import HashtagRepository from './HashtagRepository';
import UserHashtag from './../models/UserHashtag';
import Utils from './../util/Utils';

export default class UserHashtagRepository extends BaseRepository {

    constructor() {
        super();
    }

    addNew(userId, hashtagId) {
        let record = {
            user_id: userId,
            hashtag_id: hashtagId,
            created_at: Utils.generateTimestampLong(),
            updated_at: Utils.generateTimestampLong()
        };
        return UserHashtag.query().insert(record);
    }

    async updateUserInterestedSubjects(userId, hashtags) {
        let hashtagRepo = new HashtagRepository();

        //remove all user hashtag
        await this.removeAllUserHashtag(userId);
        for (let item of hashtags) {
            await hashtagRepo.addNewIfNotExist(item);
            await this.addNewIfNotExist(userId, item);
        }
    }

    removeAllUserHashtag(userId) {
        return UserHashtag.query()
            .delete()
            .where('user_id', userId);
    }

    async addNewIfNotExist(userId, hashtag) {
        let hashtagRepo = new HashtagRepository();
        let hashtagId = await hashtagRepo.findHashtagIdByName(hashtag);

        if (!(await this.checkUserHashtagExisted(userId, hashtagId))) {
            await this.addNew(userId, hashtagId);
        }
    }

    /**
     * Check if this hashtag id belongs to the user id
     * @param hashtagId
     * @param userId
     * @returns {*}
     */
    async checkUserHashtagExisted(userId, hashtagId) {
        return UserHashtag.query().pluck('user_id')
            .where('user_id', userId)
            .andWhere('hashtag_id', hashtagId).first();
    }

    getHashtagListByUserId(userId) {
        return UserHashtag.query().pluck('hashtag_id').where('user_id', userId);
    }

    /**
     * Return user id list
     * @param userId
     * @param hashtagsIds Hashtag id list
     */
    getUserIdListHaveHashtagList(userId, hashtagsIds) {
        return UserHashtag.query()
            .pluck('user_id')
            .where('hashtag_id', 'in', hashtagsIds)
            .whereNot('user_id', userId);
    }
}