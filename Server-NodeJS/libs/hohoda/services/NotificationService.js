import BaseService from "./BaseService";
import NotificationRepository from "./../repositories/NotificationRepository";

export default class NotificationService extends BaseService {

    constructor(notificationRepository) {
        super();
        if (arguments.length) {
            this._repository = notificationRepository;
        } else {
            this._repository = new NotificationRepository();
        }
    }

    getNumberOfUnreadNotifications(userId) {
        return this._repository.getNumberOfUnreadNotifications(userId);
    }

    async getAllNotifications(userId, timeNext) {
        let notifications = await this._repository.getAllNotifications(userId, timeNext)
        for (let item of notifications) {
            item.course = item.course[0];
            item.profile = item.profile[0];
        }
        return notifications;
    }

    updateNotificationStatus(userId, notificationId, status){
        return this._repository.updateNotificationStatus(userId, notificationId, status);
    }
}