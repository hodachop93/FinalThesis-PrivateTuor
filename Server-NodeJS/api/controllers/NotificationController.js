import BaseController from "./../../libs/hohoda/core/framework/BaseController";
import Container from "./../../libs/hohoda/core/Container";

export default class NotificationController extends BaseController {

    constructor(req, res) {
        super(req, res);
    }

    getAllNotifications() {
        let userId = this.request.headers['user_id'];
        let timeNext = this.request.body.time_next;

        let notificationService = Container.getService('NotificationService');
        notificationService.getAllNotifications(userId, timeNext)
            .then(notifications => {
                this.response.json({
                    status: 0,
                    messageCode: 200,
                    messageInfo: null,
                    body: notifications
                });
            })
            .catch(err => {
                console.log(err);
                this.handleErrorException(err);
            });
    }

    updateNotificationStatus() {
        let userId = this.request.headers['user_id'];
        let notificationId = this.request.body.notification_id;
        let status = this.request.body.status;

        let notificationService = Container.getService('NotificationService');
        notificationService.updateNotificationStatus(userId, notificationId, status)
            .then(() => {
                this.response.json({
                    status: 0,
                    messageCode: 200,
                    messageInfo: null,
                    body: null
                });
            })
            .catch(err => {
                console.log(err);
                this.handleErrorException(err);
            });
    }
}