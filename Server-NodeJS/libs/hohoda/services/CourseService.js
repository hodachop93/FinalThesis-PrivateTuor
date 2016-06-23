import BaseService from "./BaseService";
import UserException from "./../exceptions/UserException";
import CourseRepository from "./../repositories/CourseRepository";
import HashtagRepository from './../repositories/HashtagRepository';
import ProfileRepository from './../repositories/ProfileRepository';
import CandidateRepository from './../repositories/CandidateRepository';
import {INTERNAL_SERVER_ERROR, COURSE_STATUS_CLOSED, PUSH_NOTI_WHEN_CLOSE_COURSE_KUE} from "./../util/ApplicationConstants";
import i18n from "i18n";
import geolib from "geolib";
import request from 'request';
import config from './../../../configs/config';
import async from "async";
import queue from './../queue/QueueService';

export default class CourseService extends BaseService {

    constructor(courseRepository) {
        super();
        if (arguments.length) {
            this._repository = courseRepository;
        } else {
            this._repository = new CourseRepository();
        }
    }

    findCourseByCourseId(courseId) {
        return this._repository.findCourseByCourseId(courseId)
            .then(course => {
                return course;
            })
    }

    async searchCoursesByHashtagIds(hashtagIds, timeNext, userId) {
        try {
            let courseIds = await this._repository.searchListCourseIdsByHashtagIds(hashtagIds);
            let courses = await this._repository.searchCoursesByCourseIds(courseIds, timeNext, userId);
            return courses;
        } catch (err) {
            throw new UserException(i18n.__('msg_error_search_course_50001'), CAN_NOT_FETCH_COURSES, err.message);
        }
    }

    async searchCourseByHashtags(hashtags, timeNext, userId) {
        let hashtagRepo = new HashtagRepository();
        try {
            let hashtagIds = await hashtagRepo.getHashtagListByName(hashtags);
            let courses = await this.searchCoursesByHashtagIds(hashtagIds, timeNext, userId);
            let appendCourses = await this.searchCourseByTitle(hashtags, timeNext, userId);
            for (let item of appendCourses){
                courses.push(item);
            }
            return courses;
        } catch (err) {
            console.log(err);
            throw new UserException(i18n.__('msg_error_system_150001'), INTERNAL_SERVER_ERROR, err.message);
        }
    }

    searchCourseByTitle(keys, timeNext, userId) {
        return this._repository.searchCourseByTitle(keys, timeNext, userId);
    }

    async searchCourseByLocation(request, userId) {
        let lat = request.lat;
        let lng = request.lng;
        let radius = request.radius;
        let timeNext = request.time_next;

        let courses = await this._repository.getAllCourse(timeNext, userId);

        let validInsideCourseIds = [];
        courses.forEach(item => {
            if (!item.lat && !item.lng) {
                return;
            }
            if (this.isPointInCircle(item.lat, item.lng, lat, lng, radius)) {
                validInsideCourseIds.push(item.id);
            }
        });
        return this._repository.searchCoursesByCourseIds(validInsideCourseIds, timeNext);
    }

    async getMyCourseAsTutor(userId, timeNext) {
        try {
            let courses = await this._repository.getMyCourseAsTutor(userId, timeNext);
            return courses;
        } catch (err) {
            console.log(err);
            throw new UserException(i18n.__('msg_error_system_150001'), INTERNAL_SERVER_ERROR, err.message);
        }
    }

    async getMyCourseAsStudent(userId, timeNext) {
        try {
            let courses = await this._repository.getMyCourseAsStudent(userId, timeNext);
            return courses;
        } catch (err) {
            throw new UserException(i18n.__('msg_error_system_150001'), INTERNAL_SERVER_ERROR, err.message);
        }
    }

    async getLatestCourse(reqBody, userId) {
        let timeNext = reqBody.time_next;
        try {
            let courses = await this._repository.getLatestCourse(userId, timeNext);
            return courses;
        } catch (err) {
            throw new UserException(i18n.__('msg_error_system_150001'), INTERNAL_SERVER_ERROR, err.message);
        }
    }

    async getLocalCourse(reqBody, userId) {
        let timeNext = reqBody.time_next;

        try {
            // get user's lat, lng
            let profileRepo = new ProfileRepository();
            let latLng = await profileRepo.getLatLngUserAddress(userId);
            //let latLng = await this.getLatLngFromAddress(address);
            let req = {
                lat: latLng.lat,
                lng: latLng.lng,
                radius: 50000, // 50 km
                time_next: timeNext
            };
            return await this.searchCourseByLocation(req, userId);
        } catch (err) {
            throw new UserException(i18n.__('msg_error_system_150001'), INTERNAL_SERVER_ERROR, err.message);
        }
    }

    async getCourseDetail(reqBody, userId, candidateService) {
        let courseId = reqBody.course_id;
        let course = await this._repository.getCourseDetail(courseId, userId);

        //check if user joined this course
        let isJoined;
        if (await candidateService.checkUserHasAlreadyJoined(userId, courseId)) {
            isJoined = true;
        } else {
            isJoined = false;
        }
        course.is_joined = isJoined;

        return course;
    }

    getuserIdsInterestedCourse(userId, courseId) {
        return this._repository.getuserIdsInterestedCourse(userId, courseId);
    }

    async closeCourse(userId, courseId) {
        let course = await this.findCourseByCourseId(courseId);
        if (course.owner_id.localeCompare(userId) == 0) {
            let returnedCourse = await this._repository.updateCourseStatus(courseId, COURSE_STATUS_CLOSED);
            //push notification
            queue.create(PUSH_NOTI_WHEN_CLOSE_COURSE_KUE, {
                title: PUSH_NOTI_WHEN_CLOSE_COURSE_KUE,
                createdBy: userId,
                courseId: returnedCourse.id
            }).save();
            return returnedCourse;
        } else {
            throw new UserException(i18n.__('msg_error_course_80004'), INTERNAL_SERVER_ERROR, i18n.__('msg_error_course_80004'));
        }
    }

    getCandidateList(courseId) {
        let candidateRepo = new CandidateRepository();
        return candidateRepo.getCandidateList(courseId);
    }

    isPointInCircle(lat, lng, latCenter, lngCenter, radius) {
        return geolib.isPointInCircle(
            {latitude: lat, longitude: lng},
            {latitude: latCenter, longitude: lngCenter},
            radius
        );
    }
}