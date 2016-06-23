import {Model} from "objection";

export default class Profile extends Model {
    static get tableName() {
        return "profile";
    }

    static get relationMappings() {
        return {
            user: {
                relation: Model.OneToOneRelation,
                modelClass: __dirname + '/User',
                join: {
                    from: 'profile.user_id',
                    to: 'user.id'
                }
            },
            interested_hashtags: {
                relation: Model.ManyToManyRelation,
                modelClass: __dirname + '/Hashtag',
                join: {
                    from: "profile.user_id",
                    through: {
                        from : "user_hashtag.user_id",
                        to: "user_hashtag.hashtag_id"
                    },
                    to: "hashtag.id"
                }
            },
            review: {
                relation: Model.OneToManyRelation,
                modelClass: __dirname + '/Review',
                join: {
                    from: 'profile.user_id',
                    to: 'review.to_user_id'
                }
            }
        };
    }
}