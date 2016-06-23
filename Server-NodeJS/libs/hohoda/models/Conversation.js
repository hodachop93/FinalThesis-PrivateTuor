import {Model} from "objection";

export default class Conversation extends Model {
    static get tableName() {
        return "conversation";
    }

    static get relationMappings() {
        return {
            person_one: {
                relation: Model.OneToOneRelation,
                modelClass: __dirname + '/Profile',
                join: {
                    from: 'conversation.member_one',
                    to: 'profile.user_id'
                }
            },
            person_two: {
                relation: Model.OneToOneRelation,
                modelClass: __dirname + '/Profile',
                join: {
                    from: 'conversation.member_two',
                    to: 'profile.user_id'
                }
            }
        }
    }
}