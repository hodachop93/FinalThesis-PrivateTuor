/**
 * Created by hopho on 04/04/2016.
 */
import BaseService from "./BaseService";
import UserException from "./../exceptions/UserException";
import Container from "./../../hohoda/core/Container";
import i18n from "i18n";
import NotificationRepository from "./../repositories/NotificationRepository";
import {INTERNAL_SERVER_ERROR} from "./../util/ApplicationConstants";

import config from "./../../../configs/config";

import gcm from 'node-gcm';

export default class PushnotificationService extends BaseService {

    constructor(notificationRepository) {
        super();
        if (arguments.length) {
            this._repository = notificationRepository;
        } else {
            this._repository = new NotificationRepository();
        }
    }

    /**
     * Push notification
     * @param toUserId The user id will be received notification
     * @param message message to send
     * @param notificationId Notification id for multiple notification details
     */
    async pushNotification(toUserId, message, notificationId) {
        //save notification detail to db
        await this.saveNotificationDetail(toUserId, notificationId);

        let deviceTokenService = Container.getService("DevicetokenService");
        let notificationService = Container.getService("NotificationService");
        let deviceTokens = null;
        try {
            //get device token of a user
            deviceTokens = await deviceTokenService.getDeviceTokens(toUserId);
            //get number of unread notifications
            let numOfUnreadNoti = await notificationService.getNumberOfUnreadNotifications(toUserId);
            message.data.num_unread_notifications = numOfUnreadNoti;
        } catch (err) {
        }

        let sender = new gcm.Sender(config[process.env.NODE_ENV].gcm_api_key);

        let pushMessage = {
            data: {
                notification_id: notificationId,
                course_id: message.data.course_id,
                push_type: message.data.push_type,
                title: message.notification.title,
                body: message.notification.body,
                num_unread_notifications: message.data.num_unread_notifications,
                created_by: message.data.created_by
            }
        };
        let gcmMessage = new gcm.Message(pushMessage);

        if (deviceTokens.length != 0) {
            return new Promise((resolve, reject) => {
                sender.send(gcmMessage, {registrationTokens: deviceTokens}, function (err, response) {
                    if (err) {
                        console.log(err);
                        reject(err);
                    }
                    else {
                        console.log(response);
                        resolve(response);
                    }
                });
            });
        }

    }

    async sendMessage(message, sendTo) {
        let deviceTokenService = Container.getService("DevicetokenService");
        let deviceTokens = null;
        try {
            //get device token of a user
            deviceTokens = await deviceTokenService.getDeviceTokens(sendTo);
        } catch (err) {
            console.log(err);
        }
        let gcmMessage = new gcm.Message(message);
        let sender = new gcm.Sender(config[process.env.NODE_ENV].gcm_api_key);

        if (deviceTokens.length != 0) {
            return new Promise((resolve, reject) => {
                sender.send(gcmMessage, {registrationTokens: deviceTokens}, function (err, response) {
                    if (err) {
                        console.log(err);
                        reject(err);
                    }
                    else {
                        console.log(response);
                        resolve(response);
                    }
                });
            });
        }
    }

    /**
     * Save notification table
     * @param message
     */
    async saveNotification(message) {
        try {
            let notification = await this._repository.saveNotification(message);
            return notification.id;
        } catch (err) {
            console.log(err);
        }
    }

    /**
     * Save notification detail table
     * @param toUserId
     * @param notificationId
     */
    async saveNotificationDetail(toUserId, notificationId) {
        try {
            await this._repository.saveNotificationDetail(toUserId, notificationId);
        } catch (err) {
            console.log(err);
        }

    }
}