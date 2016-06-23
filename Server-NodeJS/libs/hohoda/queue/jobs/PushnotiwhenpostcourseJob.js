import Container from "./../../core/Container";
import Utils from "./../../util/Utils";
import i18n from "i18n";
import {
    PUSH_NOTI_WHEN_POST_COURSE_KUE,
    MAX_ACTIVE_JOBS,
    HOHODA_APPLICATION_NAME,
    PUSH_NOTI_WHEN_POST_COURSE_TYPE
} from "./../../util/ApplicationConstants";

module.exports = function process(queue) {
    queue.process(PUSH_NOTI_WHEN_POST_COURSE_KUE, MAX_ACTIVE_JOBS, async function (job, done) {
		
        let pushNotiService = Container.getService("PushnotificationService");
        let courseService = Container.getService("CourseService");

        //get user id list to send notification
        let userIds = await courseService.getuserIdsInterestedCourse(job.data.createdBy, job.data.courseId);

        let course = await courseService.findCourseByCourseId(job.data.courseId);

        let message = {
            data: {
                created_by: job.data.createdBy,
                course_id: job.data.courseId,
                push_type: PUSH_NOTI_WHEN_POST_COURSE_TYPE
            },
            notification: {
                title: HOHODA_APPLICATION_NAME,
                body: i18n.__('push_noti_when_post_course_1') + course.title + i18n.__('push_noti_when_post_course_2')
            }
        };

        //save notification to db
        let notificationId = await pushNotiService.saveNotification(message);

        for (let userId of userIds) {
            pushNotiService.pushNotification(userId, message, notificationId);
        }

        done();

    });
};