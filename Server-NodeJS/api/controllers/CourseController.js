import BaseController from "./../../libs/hohoda/core/framework/BaseController";
import Container from "./../../libs/hohoda/core/Container";
import Utils from './../../libs/hohoda/util/Utils';
// import queue from './../../libs/hohoda/queue/queueService';

export default class CourseController extends BaseController {

    async getMyCourseAsTutor() {
        let userId = this.request.headers['user_id'];
        let timeNext = this.request.body.time_next;

        let courseService = Container.getService('CourseService');
        try {
            let courses = await courseService.getMyCourseAsTutor(userId, timeNext)
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

    async getMyCourseAsStudent() {
        let userId = this.request.headers['user_id'];
        let timeNext = this.request.body.time_next;

        let courseService = Container.getService('CourseService');
        try {
            let courses = await courseService.getMyCourseAsStudent(userId, timeNext)
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

    async searchCourseByLocation() {
        let reqBody = this.request.body;
        let userId = this.request.headers['user_id'];

        let courseService = Container.getService('CourseService');
        try {
            let courses = await courseService.searchCourseByLocation(reqBody, userId);
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

    async getLatestCourse() {
        let reqBody = this.request.body;
        let userId = this.request.headers['user_id'];
        let courseService = Container.getService('CourseService');
        try {
            let courses = await courseService.getLatestCourse(reqBody, userId);
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

    async getLocalCourse() {
        let reqBody = this.request.body;
        let userId = this.request.headers['user_id'];
        let courseService = Container.getService('CourseService');
        try {
            let courses = await courseService.getLocalCourse(reqBody, userId);
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

    async getCourseDetail() {
        let reqBody = this.request.body;
        let userId = this.request.headers['user_id'];
        let courseService = Container.getService('CourseService');
        let candidateService = Container.getService('CandidateService');
        try {
            let courses = await courseService.getCourseDetail(reqBody, userId, candidateService);
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

    async unJoinCourse() {
        let userId = this.request.headers['user_id'];
        let courseId = this.request.body.course_id;
        let candidateService = Container.getService('CandidateService');

        candidateService.removeCandidate(userId, courseId)
            .then(()=> {
                this.response.json({
                    status: 0,
                    messageCode: 200,
                    messageInfo: null,
                    body: null
                });
            })
            .catch(err=> {
                console.log(err);
                this.handleErrorException(err);
            })

    }

    async closeCourse() {
        let userId = this.request.headers['user_id'];
        let courseId = this.request.body.course_id;
        let courseService = Container.getService('CourseService');

        courseService.closeCourse(userId, courseId)
            .then(()=> {
                this.response.json({
                    status: 0,
                    messageCode: 200,
                    messageInfo: null,
                    body: null
                });
            })
            .catch(err=> {
                console.log(err);
                this.handleErrorException(err);
            })

    }

    getCandidateList() {
        let courseId = this.request.body.course_id;
        let courseService = Container.getService('CourseService');

        courseService.getCandidateList(courseId)
            .then(candidates => {
                this.response.json({
                    status: 0,
                    messageCode: 200,
                    messageInfo: null,
                    body: candidates
                });
            })
            .catch(err => {
                console.log(err);
                this.handleErrorException(err);
            })
    }

}