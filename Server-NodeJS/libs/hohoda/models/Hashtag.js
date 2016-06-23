import {Model} from "objection";

export default class Hashtag extends Model{
    static get tableName(){
        return "hashtag";
    }

    static get relationMappings(){
        return {
            isPopularhashtag: {
                relation: Model.OneToOneRelation,
                modelClass: __dirname + '/Popularhashtag',
                join: {
                    from: 'hashtag.id',
                    to: 'popular_hashtag.id'
                }
            }
        };
    }
}