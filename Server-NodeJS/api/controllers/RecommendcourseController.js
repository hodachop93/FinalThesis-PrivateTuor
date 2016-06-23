import BaseController from "./../../libs/hohoda/core/framework/BaseController";
import Container from "./../../libs/hohoda/core/Container";

export default class RecommendcourseController extends BaseController {

    async getRecommendCourses() {
        let userId = this.request.headers['user_id'];
        let timeNext = this.request.body.time_next;

        let recommendCourseService = Container.getService("RecommendcourseService");
        let courseService = Container.getService("CourseService");

        let request = this.buildRecommendCourseRequest(userId, timeNext);
        try {
            let courses = await recommendCourseService.getRecommendedCourses(request, courseService);
            this.response.json({
                status: 0,
                messageCode: 200,
                messageInfo: null,
                body: courses
            });
        } catch (err) {
            this.handleErrorException(err);
        }
    }

    buildRecommendCourseRequest(userId, timeNext) {
        return {
            userId: userId,
            timeNext: timeNext
        }
    }
}