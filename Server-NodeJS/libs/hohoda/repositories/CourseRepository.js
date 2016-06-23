import BaseRepository from "./BaseRepository";
import CourseHashtagRepository from "./CourseHashtagRepository";
import HashtagRepository from "./HashtagRepository";
import Course from "./../models/Course";
import Hashtag from "./../models/Hashtag";
import UserHashtag from './../models/UserHashtag';
import CourseHashtag from "./../models/CourseHashtag";
import Candidate from "./../models/Candidate";
import Profile from "./../models/Profile";
import Utils from "./../util/Utils";
import async from "async";
import {
    MAX_ITEMS_PER_LOAD, COURSE_STATUS_POSTED
}
    from "./../util/ApplicationConstants";
import * as objection from 'objection';

export default class CourseRepository extends BaseRepository {

    constructor() {
        super();
    }

    findCourseByCourseId(courseId) {
        return Course.query().where("course.id", courseId).first();
    }

    addNew(courseReq, hashtags) {
        courseReq.created_at = Utils.generateTimestampLong();
        courseReq.updated_at = Utils.generateTimestampLong();

        let hashtagRepository = new HashtagRepository();
        let courseHashtagRepository = new CourseHashtagRepository();
        //post a new course
        return addNewCourse();


        //define private method
        function addNewCourse() {
            return objection.transaction(Course, (Course) => {
                return Course.query().insert(courseReq)
                    .then(course => {
                        if (course) {
                            return addCourseHashtags();
                        }
                    });
            });
        };

        function addCourseHashtags() {
            return new Promise(function (resolve, reject) {
                async.each(hashtags, function (item, callback) {
                    hashtagRepository.findHashtagByName(item.trim())
                        .then(hashtag => {
                            if (hashtag == null) {
                                return hashtagRepository.addNew(item);
                            } else {
                                return hashtag;
                            }
                        })
                        .then(hashtag => {
                            if (hashtag != null) {
                                let courseHashtag = {
                                    course_id: courseReq.id,
                                    hashtag_id: hashtag.id
                                };
                                return courseHashtagRepository.addNew(courseHashtag);
                            }
                        })
                        .then(() => {
                            callback();
                            resolve(hashtags);
                        });
                }, function (err) {
                    if (err) {
                        reject(err);
                    }
                });
            });
        };
    }

    async searchListCourseIdsByHashtagIds(hashtagIds) {
        let courseIds = await CourseHashtag.query()
            .pluck('course_id')
            .distinct('course_id')
            .where('hashtag_id', 'in', hashtagIds);
        return courseIds;
    }

    async searchCoursesByCourseIds(courseIds, timeNext, userId) {
        let courses = await Course.query()
            .eager('[hashtags, profile]')
            .pick(Profile, ['name', 'avatar_url', 'rate_average', 'infor'])
            .pick(Hashtag, ['name'])
            .select('course.*')
            .count('candidate.id as number_candidates')
            .leftJoin('candidate', 'course.id', 'candidate.course_id')
            .andWhere('course.status', COURSE_STATUS_POSTED)
            .andWhere('course.id', 'in', courseIds)
            .andWhere("course.created_at", "<", timeNext)
            .whereNot('course.owner_id', userId)
            .groupBy('course.id')
            .orderBy('course.created_at', 'desc')
            .limit(MAX_ITEMS_PER_LOAD);
        return courses;
    }

    getMyCourseAsTutor(userId, timeNext) {
        return Course.query()
            .eager('[hashtags, profile]')
            .pick(Profile, ['name', 'avatar_url', 'rate_average', 'infor'])
            .pick(Hashtag, ['name'])
            .select('course.*')
            .count('candidate.id as number_candidates')
            .leftJoin('candidate', 'course.id', 'candidate.course_id')
            .andWhere("course.created_at", "<", timeNext)
            .andWhere('course.owner_id', userId)
            .groupBy('course.id')
            .orderBy('course.created_at', 'desc')
            .limit(MAX_ITEMS_PER_LOAD);
    }

    getMyCourseAsStudent(userId, timeNext) {
        return Course.query()
            .eager('[hashtags, profile]')
            .pick(Profile, ['name', 'avatar_url', 'rate_average', 'infor'])
            .pick(Hashtag, ['name'])
            .select('course.*')
            .count('candidate.id as number_candidates')
            .leftJoin('candidate', 'course.id', 'candidate.course_id')
            .andWhere('candidate.user_id', userId)
            .andWhere("course.created_at", "<", timeNext)
            .groupBy('course.id')
            .orderBy('course.created_at', 'desc')
            .limit(MAX_ITEMS_PER_LOAD);
    }

    getAllCourse(timeNext, userId) {
        return Course.query()
            .where('created_at', '<', timeNext)
            .whereNot('owner_id', userId)
            .groupBy('created_at')
            .orderBy('created_at', 'desc')
            .limit(MAX_ITEMS_PER_LOAD);
    }

    getLatestCourse(userId, timeNext) {
        return Course.query()
            .eager('[hashtags, profile]')
            .pick(Profile, ['name', 'avatar_url', 'rate_average', 'infor'])
            .pick(Hashtag, ['name'])
            .select('course.*')
            .count('candidate.id as number_candidates')
            .leftJoin('candidate', 'course.id', 'candidate.course_id')
            .whereNot('owner_id', userId)
            .andWhere("course.created_at", "<", timeNext)
            .groupBy("course.id")
            .orderBy("course.created_at", 'desc')
            .limit(MAX_ITEMS_PER_LOAD);
    }

    /**
     * Get course detail
     * @param courseId The course id
     * @param userId The user id of current user
     */
    getCourseDetail(courseId, userId) {
        return Course.query()
            .eager('[hashtags, profile]')
            .pick(Profile, ['name', 'avatar_url', 'rate_average', 'infor'])
            .pick(Hashtag, ['name'])
            .select('course.*')
            .count('candidate.id as number_candidates')
            .leftJoin('candidate', 'course.id', 'candidate.course_id')
            .where('course.id', courseId)
            .first();
    }

    getuserIdsInterestedCourse(userId, courseId) {
        return CourseHashtag.query().pluck('hashtag_id').where('course_id', courseId)
            .then(hashtagIds => {
                return UserHashtag.query()
                    .distinct('user_id')
                    .pluck('user_id')
                    .where('hashtag_id', 'in', hashtagIds)
                    .whereNot('user_id', userId);
            });
    }

    updateCourseStatus(courseId, status) {
        return Course.query().patch({status: status}).where('id', courseId);
    }

    getCandidateList(courseId) {
        return Candidate.query().pluck('user_id').where('course_id', courseId);
    }

    searchCourseByTitle(keys, timeNext, userId) {
        let listSearch = '%';
        if (Utils.isString(keys)) {
            listSearch += keys;
        } else {
            listSearch += keys[0];
            for (let i = 1; i < keys.length; i++){
                listSearch += ' ' + keys[i];
            }
        }
        listSearch += '%';

        return Course.query()
            .eager('[hashtags, profile]')
            .pick(Profile, ['name', 'avatar_url', 'rate_average', 'infor'])
            .pick(Hashtag, ['name'])
            .select('course.*')
            .count('candidate.id as number_candidates')
            .leftJoin('candidate', 'course.id', 'candidate.course_id')
            .andWhere('course.status', COURSE_STATUS_POSTED)
            .andWhere("course.created_at", "<", timeNext)
            .whereNot('course.owner_id', userId)
            .andWhere('course.title', 'like', listSearch)
            .groupBy('course.id')
            .orderBy('course.created_at', 'desc')
            .limit(MAX_ITEMS_PER_LOAD);
    }
}