import UserService from "./UserService";
import CourseRepository from './../repositories/CourseRepository';
import Utils from "./../util/Utils";
import UserException from "./../exceptions/UserException";
import i18n from "i18n";
import {COURSE_STATUS_POSTED, CAN_NOT_POST_A_COURSE, PUSH_NOTI_WHEN_POST_COURSE_KUE} from "./../util/ApplicationConstants";
import queue from './../queue/QueueService';

export default class PostcourseService extends UserService {

    constructor(courseRepository) {
        super();
        if (arguments.length) {
            this._repository = courseRepository;
        } else {
            this._repository = new CourseRepository();
        }
    }

    postCourse(userId, courseReq) {
        let hashtags = courseReq.hashtags;
        let uuid = Utils.generateUUID();
        let newCourse = this.buidCourseRequest(userId, courseReq, uuid);
        return this._repository.addNew(newCourse, hashtags)
            .then(() => {
                //push notification
                queue.create(PUSH_NOTI_WHEN_POST_COURSE_KUE, {
                    title: PUSH_NOTI_WHEN_POST_COURSE_KUE,
                    createdBy: userId,
                    courseId: newCourse.id
                }).save();
                return newCourse.id;
            })
            .catch(err => {
                throw new UserException(i18n.__('msg_error_course_80001'), CAN_NOT_POST_A_COURSE, err.message, null);
            });
    }

    buidCourseRequest(userId, courseReq, uuid) {
        let course = {
            id: uuid,
            owner_id: userId,
            title: courseReq.title,
            price: courseReq.price,
            duration: courseReq.duration,
            description: courseReq.description,
            start_date: courseReq.start_date,
            lat: courseReq.lat,
            lng: courseReq.lng,
            address: courseReq.address,
            status: COURSE_STATUS_POSTED,
            course_type: courseReq.course_type,
            schedule: courseReq.schedule
        }
        return course;
    }
}