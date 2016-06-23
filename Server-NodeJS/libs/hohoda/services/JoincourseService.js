import BaseService from "./BaseService";
import CandidateRepository from "./../repositories/CandidateRepository";
import UserException from "./../exceptions/UserException";
import i18n from "i18n";
import queue from './../queue/QueueService';
import {
    INTERNAL_SERVER_ERROR, COURSE_WAS_NOT_POSTED, CAN_NOT_JOIN_COURSE,
    HAVE_ALREADY_JOINED_COURSE, COURSE_NOT_FOUND, COURSE_STATUS_POSTED,
    PUSH_NOTI_WHEN_JOIN_COURSE_KUE
} from "./../util/ApplicationConstants";

export default class JoincourseService extends BaseService {

    constructor(candidateRepository) {
        super()
        if (arguments.length) {
            this._repository = candidateRepository;
        } else {
            this._repository = new CandidateRepository();
        }
    }

    joinClass(userId, candidateReq, courseService) {
        let courseId = candidateReq.course_id;
        return courseService.findCourseByCourseId(courseId)
            .then(course => {
                if (course == null) {
                    throw new UserException(i18n.__('msg_error_course_80002'),
                        COURSE_NOT_FOUND, i18n.__('msg_error_course_80002'), null);
                } else {
                    if (course.status != COURSE_STATUS_POSTED) {
                        throw new UserException(i18n.__('msg_error_candidate_70002'),
                            COURSE_WAS_NOT_POSTED, i18n.__('msg_error_candidate_70002'), null);
                    } else {
                        if (course.owner_id == userId) {
                            throw new UserException(i18n.__('msg_error_candidate_70003'),
                                CAN_NOT_JOIN_COURSE, i18n.__('msg_error_candidate_70003'), null);
                        } else {
                            return this._repository.checkUserHasAlreadyJoined(userId, course.id)
                                .then(candidate => {
                                    if (candidate == null) {
                                        candidateReq.user_id = userId;
                                        return this._repository.createCandidate(candidateReq)
                                            .then((candidate) => {
                                                //push notification
                                                queue.create(PUSH_NOTI_WHEN_JOIN_COURSE_KUE, {
                                                    title: PUSH_NOTI_WHEN_JOIN_COURSE_KUE,
                                                    createdBy: userId,
                                                    courseId: courseId
                                                }).save();
                                                return candidate;
                                            })
                                    }
                                    else {
                                        throw new UserException(i18n.__('msg_error_candidate_70006'),
                                            HAVE_ALREADY_JOINED_COURSE, i18n.__('msg_error_candidate_70006'), null);
                                    }
                                })
                                .catch(err => {
                                    if (err.message == i18n.__('msg_error_candidate_70006')) {
                                        throw new UserException(i18n.__('msg_error_candidate_70006'),
                                            HAVE_ALREADY_JOINED_COURSE, i18n.__('msg_error_candidate_70006'), null);
                                    }
                                    else {
                                        throw new UserException(i18n.__('msg_error_system_150001'),
                                            INTERNAL_SERVER_ERROR, i18n.__('msg_error_system_150001'), null);
                                    }
                                })
                        }
                    }
                }

            })
    }

}