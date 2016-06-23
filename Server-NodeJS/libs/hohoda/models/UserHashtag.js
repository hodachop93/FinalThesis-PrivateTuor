import {Model} from "objection";

export default class Userhashtag extends Model {
    static get tableName() {
        return "user_hashtag";
    }

    static get relationMappings() {
        return {
            profile: {
                relation: Model.OneToOneRelation,
                modelClass: __dirname + '/Profile',
                join: {
                    from: 'user_hashtag.user_id',
                    to: 'profile.user_id'
                }
            }
        }
    }
}