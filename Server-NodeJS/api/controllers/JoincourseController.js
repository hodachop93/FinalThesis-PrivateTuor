
import BaseController from "./../../libs/hohoda/core/framework/BaseController";
import Container from "./../../libs/hohoda/core/Container";
import Utils from './../../libs/hohoda/util/Utils';
 import queue from './../../libs/hohoda/queue/QueueService';

export default class JoincourseController extends BaseController {
    joinCourse() {

        let userId = this.request.headers['user_id'];
        let candidateReq = this.request.body;

        let joinCourseService = Container.getService("JoincourseService");
        let courseService = Container.getService("CourseService");
        joinCourseService.joinClass(userId, candidateReq, courseService)
            .then(() => {
                this.response.json({
                    status: 0,
                    messageCode: 200,
                    messageInfo: null,
                    body: null
                })
            })
            .catch(err => {
                this.handleErrorException(err);
            })
    }
}