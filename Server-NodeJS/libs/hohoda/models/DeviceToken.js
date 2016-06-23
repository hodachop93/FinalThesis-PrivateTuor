import {Model} from "objection";

export default class LoginDevice extends Model {
    static get tableName() {
        return 'device_token';
    }

    static get relationMappings() {
        return {
            user: {
                relation: Model.OneToManyRelation,
                modelClass: __dirname + '/User',
                join: {
                    from: 'device_token.user_id',
                    to: 'user.id'
                }
            }
        }
    }
}