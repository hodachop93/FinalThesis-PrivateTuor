import {Model} from "objection";

export default class Candidate extends Model {
    static get tableName() {
        return "candidate";
    }

    static get relationMappings() {
        return {
            profile: {
                relation: Model.OneToOneRelation,
                modelClass: __dirname + '/Profile',
                join: {
                    from: 'candidate.user_id',
                    to: 'profile.user_id'
                }
            },
            course: {
                relation: Model.OneToOneRelation,
                modelClass: __dirname + '/Course',
                join: {
                    from: 'candidate.course_id',
                    to: 'course.id'
                }
            }
        };
    }
}