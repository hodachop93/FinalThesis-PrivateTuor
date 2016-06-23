import {Model} from "objection";

export default class CourseHashtag extends Model {
    static get tableName() {
        return "course_hashtag";
    }
}