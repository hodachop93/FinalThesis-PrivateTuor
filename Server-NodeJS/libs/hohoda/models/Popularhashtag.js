import {Model} from "objection";

export default class Popularhashtag extends Model {
    static get tableName() {
        return "popular_hashtag";
    }

    static get relationMappings() {
        return {
            hashtag: {
                relation: Model.OneToOneRelation,
                modelClass: __dirname + '/Hashtag',
                join: {
                    from: 'popular_hashtag.id',
                    to: 'hashtag.id'
                }
            }
        };
    }
}