import BaseController from "./../../libs/hohoda/core/framework/BaseController";
import Container from "./../../libs/hohoda/core/Container";

export default class SearchcoursebyhashtagController extends BaseController {

    async searchCourseByHashtag() {
        let userId = this.request.headers['user_id'];
        let reqBody = this.request.body;

        let courseService = Container.getService('CourseService');
        try {
            let courses = await courseService.searchCourseByHashtags(reqBody.hashtags, reqBody.time_next, userId);
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
}
