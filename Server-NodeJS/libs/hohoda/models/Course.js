import {Model} from "objection";

export default class Course extends Model{
    static get tableName(){
        return "course";
    }

    static get relationMappings(){
        return {
            hashtags: {
                relation: Model.ManyToManyRelation,
                modelClass: __dirname + '/Hashtag',
                join: {
                    from: 'course.id',
                    through: {
                        from: 'course_hashtag.course_id',
                        to: 'course_hashtag.hashtag_id'
                    },
                    to: 'hashtag.id'
                }
            },
            candidates: {
                relation: Model.OneToManyRelation,
                modelClass: __dirname + '/Candidate',
                join: {
                    from: 'course.id',
                    to: 'candidate.course_id'
                }
            },
            profile: {
                relation: Model.OneToOneRelation,
                modelClass: __dirname + '/Profile',
                join: {
                    from: 'course.owner_id',
                    to: 'profile.user_id'
                }
            }
        };
    }
}