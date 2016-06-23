import UserService from "./UserService";
import UserException from "./../exceptions/UserException";
import RecommendCourseRepository from "./../repositories/RecommendCourseRepository";
import i18n from "i18n";
import {CAN_NOT_FETCH_COURSES} from "./../util/ApplicationConstants";

export default class RecommendcourseService extends UserService {

    constructor(recommendCourseRepository) {
        super();
        if (arguments.length) {
            this._repository = recommendCourseRepository;
        } else {
            this._repository = new RecommendCourseRepository();
        }
    }

    async getRecommendedCourses(request, courseService) {
        try {
            let hashtagIds = await this._repository.getHashtagIdsOfUser(request.userId);
            let courses = await courseService.searchCoursesByHashtagIds(hashtagIds, request.timeNext, request.userId);

            return courses;
        } catch (err) {
            throw new UserException(i18n.__('msg_error_search_course_50001'), CAN_NOT_FETCH_COURSES, err.message);
        }
    }
}

