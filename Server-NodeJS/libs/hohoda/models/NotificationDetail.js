import {Model} from "objection";

export default class NotificationDetail extends Model {
    static get tableName() {
        return "notification_detail";
    }

    static get relationMappings() {
        return {
            notification: {
                relation: Model.OneToOneRelation,
                modelClass: __dirname + '/Notification',
                join:{
                    from: 'notification_detail.notification_id',
                    to: 'notification.id'
                }
            },
            course: {
                relation: Model.ManyToManyRelation,
                modelClass: __dirname + '/Course',
                join: {
                    from: 'notification_detail.notification_id',
                    through: {
                        from: 'notification.id',
                        to: 'notification.course_id'
                    },
                    to: 'course.id'
                }
            },
            profile: {
                relation: Model.ManyToManyRelation,
                modelClass: __dirname + '/Profile',
                join: {
                    from: 'notification_detail.notification_id',
                    through: {
                        from: 'notification.id',
                        to: 'notification.created_by'
                    },
                    to: 'profile.user_id'
                }
            }
        };
    }
}