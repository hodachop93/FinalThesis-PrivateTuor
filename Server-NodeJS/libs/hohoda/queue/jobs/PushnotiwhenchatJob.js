import Container from "./../../core/Container";
import Utils from "./../../util/Utils";
import i18n from "i18n";
import MessageRepository from './../../repositories/MessageRepository';
import {
    PUSH_NOTI_WHEN_CHAT_KUE,
    MAX_ACTIVE_JOBS,
    HOHODA_APPLICATION_NAME,
    PUSH_NOTI_WHEN_CHAT_TYPE
} from "./../../util/ApplicationConstants";

module.exports = function process(queue) {
    queue.process(PUSH_NOTI_WHEN_CHAT_KUE, MAX_ACTIVE_JOBS, async function (job, done) {
        let pushNotiService = Container.getService("PushnotificationService");
        let profileService = Container.getService("ProfileService");

        let messageRepo = new MessageRepository();
        let conversation;
        try {
            conversation = await messageRepo.getConversation(job.data.conversationId);
        } catch (err) {
            console.log(err);
        }
        let sendTo;
        if (conversation.member_one == job.data.createdBy) {
            sendTo = conversation.member_two;
        } else {
            sendTo = conversation.member_one;
        }

        let profile = await profileService.getProfile(job.data.createdBy);

        let mess = {
            data: {
                title: HOHODA_APPLICATION_NAME,
                //body: profile.name + i18n.__('push_noti_when_chat'),
                body: profile.name + ": " + job.data.content,
                push_type: PUSH_NOTI_WHEN_CHAT_TYPE,
                content: job.data.content,
                created_by: job.data.createdBy,
                conversation_id: job.data.conversationId,
                message_id: job.data.messageId,
                created_at: job.data.createdAt
            }
        };

        pushNotiService.sendMessage(mess, sendTo);
        done();
    });
};