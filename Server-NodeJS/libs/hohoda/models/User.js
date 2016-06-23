import {Model} from "objection";

export default class User extends Model{
    static get tableName() {
        return "user";
    }

    static get relationMappings(){
        return {
            hasProfile: {
                relation: Model.OneToManyRelation,
                modelClass: __dirname + '/Profile',
                join: {
                    from: 'user.id',
                    to: 'profile.user_id'
                }
            }
        }
    }
}