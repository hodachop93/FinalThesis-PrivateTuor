import BaseRepository from "./BaseRepository";
import Hashtag from "./../models/Hashtag";
import Utils from "./../util/Utils";


export default class HashtagRepository extends BaseRepository {
    constructor() {
        super();
    }

    addNew(nameHashtag) {
        let hashtag = {
            id: Utils.generateUUID(),
            name: nameHashtag.trim(),
            created_at: Utils.generateTimestampLong(),
            updated_at: Utils.generateTimestampLong()
        };
        return Hashtag.query().insert(hashtag);
    }

    async addNewIfNotExist(nameHashtag) {
        let hashtag = await this.findHashtagByName(nameHashtag);
        if (!hashtag) {
            return this.addNew(nameHashtag);
        }
    }

    findHashtagByName(name) {
        return Hashtag.query().where("name", name).first();
    }

    findHashtagIdByName(name) {
        return Hashtag.query().pluck('id').where("name", name).first();
    }

    getHashtagListByName(hashtags) {
        return Hashtag.query().pluck('id').whereIn('name', hashtags);
    }

}