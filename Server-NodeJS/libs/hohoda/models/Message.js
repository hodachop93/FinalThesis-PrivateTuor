import {Model} from "objection";

export default class Message extends Model {
    static get tableName() {
        return "message";
    }

    static get relationMappings() {
        return {
            user: {
                relation: Model.OneToOneRelation,
                modelClass: __dirname + '/Profile',
                join: {
                    from: 'message.user_id',
                    to: 'profile.user_id'
                }
            }
        };
    }
}