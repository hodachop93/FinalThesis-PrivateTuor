import BaseRepository from "./BaseRepository";
import Notification from "./../models/Notification";
import NotificationDetail from "./../models/NotificationDetail";
import Utils from "./../util/Utils";
import {
    MAX_ITEMS_PER_LOAD
}
    from "./../util/ApplicationConstants";

export default class NotificationRepository extends BaseRepository {

    constructor() {
        super();
    }

    saveNotification(message) {
        let data = message.data;
        let id = Utils.generateUUID();
        let noti = {
            id: id,
            noti_type: data.push_type,
            course_id: data.course_id,
            created_by: data.created_by,
            created_at: Utils.generateTimestampLong()
        };
        return Notification.query().insert(noti);
    }

    saveNotificationDetail(toUserId, notificationId) {
        let notiDetail = {
            notification_id: notificationId,
            to_user_id: toUserId,
            created_at: Utils.generateTimestampLong()
        };

        return NotificationDetail.query().insert(notiDetail);
    }

    getNumberOfUnreadNotifications(userId) {
        return NotificationDetail.query().count("* as number").where('status', 0).andWhere('to_user_id', userId)
            .then(result => {
                let number = result[0].number;
                return number;
            })
    }

    getAllNotifications(userId, timeNext) {
        return NotificationDetail.query()
            .eager('[course, profile]')
            .select('notification_detail.*', 'notification.noti_type')
            .join('notification', 'notification_detail.notification_id', 'notification.id')
            .where('notification_detail.to_user_id', userId)
            .andWhere('notification_detail.created_at', '<', timeNext)
            .orderBy('notification_detail.created_at', 'desc')
            .limit(MAX_ITEMS_PER_LOAD);
    }

    updateNotificationStatus(userId, notificationId, status) {
        return NotificationDetail.query()
            .patch({
                status: status
            })
            .where('notification_id', notificationId)
            .andWhere('to_user_id', userId);
    }
}