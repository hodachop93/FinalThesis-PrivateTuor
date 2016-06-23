
import BaseRepository from "./BaseRepository";
import CourseHashtag from "./../models/CourseHashtag";
import Utils from "./../util/Utils";

export default class CourseHashtagRepository extends BaseRepository {

    constructor() {
        super();
    }

    addNew(courseHashtag) {
        courseHashtag.created_at = Utils.generateTimestampLong();
        courseHashtag.updated_at = Utils.generateTimestampLong();
        return CourseHashtag.query().insert(courseHashtag);
    }

}