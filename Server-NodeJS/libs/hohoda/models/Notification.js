import {Model} from "objection";

export default class Notification extends Model{
    static get tableName(){
        return "notification";
    }

    static get relationMappings(){
        return {
            notificationDetail: {
                relation: Model.OneToManyRelation,
                modelClass: __dirname + '/NotificationDetail',
                join: {
                    from: 'notification.id',
                    to: 'notification_detail.notification_id'
                }
            }
        };
    }
}