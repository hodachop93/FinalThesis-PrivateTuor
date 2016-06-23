import Container from "./../../core/Container";
import Utils from "./../../util/Utils";
import i18n from "i18n";
import {
    PUSH_NOTI_WHEN_CLOSE_COURSE_KUE,
    MAX_ACTIVE_JOBS,
    HOHODA_APPLICATION_NAME,
    PUSH_NOTI_WHEN_CLOSE_COURSE_TYPE
} from "./../../util/ApplicationConstants";

module.exports = function process(queue){
    queue.process(PUSH_NOTI_WHEN_CLOSE_COURSE_KUE, MAX_ACTIVE_JOBS, async function (job, done){
        let pushNotiService = Container.getService("PushnotificationService");
        let courseService = Container.getService("CourseService");
        let profileService = Container.getService('ProfileService');

        let course = await courseService.findCourseByCourseId(job.data.courseId);
        let candidateList = await courseService.getCandidateList(course.id);
        let sendToIds = [];
        for (let item of candidateList){
            sendToIds.push(item.user_id);
        }
        let profile = await profileService.getProfile(job.data.createdBy);

        let message = {
            data: {
                created_by: job.data.createdBy,
                course_id: job.data.courseId,
                push_type: PUSH_NOTI_WHEN_CLOSE_COURSE_TYPE
            },
            notification: {
                title: HOHODA_APPLICATION_NAME,
                body: profile.name + i18n.__('push_noti_when_join_course_1') + course.title + i18n.__('push_noti_when_join_course_2')
            }
        };

        //save notification to db
        let notificationId = await pushNotiService.saveNotification(message);

        for (let userId of sendToIds) {
            pushNotiService.pushNotification(userId, message, notificationId);
        }
        done();
    });

};