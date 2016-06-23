import {Model} from "objection";

export default class Setting extends Model {
    static get tableName() {
        return "setting";
    }

    static get relationMappings() {
        return {
            user: {
                relation: Model.OneToOneRelation,
                modelClass: __dirname + '/User',
                join: {
                    from: 'setting.user_id',
                    to: 'user.id'
                }
            }
        };
    }
}