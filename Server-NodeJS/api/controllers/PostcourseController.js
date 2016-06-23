import BaseController from "./../../libs/hohoda/core/framework/BaseController";
import Container from "./../../libs/hohoda/core/Container";

export default class PostcourseController extends BaseController{
    postCourse(){
        let userID = this.request.headers['user_id'];
        let courseReq = this.request.body;

        let postCourseService = Container.getService('PostcourseService');
        postCourseService.postCourse(userID, courseReq)
            .then(courseId => {
                this.response.json({
                    status: 0,
                    messageCode: 200,
                    messageInfo: null,
                    body: courseId
                })
            })
            .catch(err => {
                this.handleErrorException(err);
            });
    }
}
