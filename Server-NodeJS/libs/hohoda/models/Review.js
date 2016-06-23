import {Model} from "objection";

export default class Profile extends Model {
    static get tableName() {
        return "review";
    }

    static get relationMappings() {
        return {
            reviewer: {
                relation: Model.OneToOneRelation,
                modelClass: __dirname + '/Profile',
                join: {
                    from: "review.reviewer_id",
                    to: "profile.user_id"
                }
            },
            toUserId: {
                relation: Model.OneToOneRelation,
                modelClass: __dirname + '/Profile',
                join: {
                    from: "review.to_user_id",
                    to: "profile.user_id"
                }
            }
        };
    }
}