import BaseService from "./BaseService";
import MessageRepository from './../repositories/MessageRepository';
import {PUSH_NOTI_WHEN_CHAT_KUE} from "./../util/ApplicationConstants";
import queue from './../queue/QueueService';

export default class MessageService extends BaseService {

    constructor(messageRepo) {
        super();
        if (arguments.length) {
            this._repository = messageRepo;
        } else {
            this._repository = new MessageRepository();
        }
    }

    startConversation(fromId, toId) {
        return this._repository.startConversation(fromId, toId)
    }

    async addNew(userId, addMessReq) {
        let message = await this._repository.addNew(userId, addMessReq);
        //push notification
        queue.create(PUSH_NOTI_WHEN_CHAT_KUE, {
            title: PUSH_NOTI_WHEN_CHAT_KUE,
            createdBy: message.user_id,
            conversationId: message.conversation_id,
            messageId: message.id,
            content: message.content,
            createdAt: message.created_at
        }).priority('high').save();
        return message;
    }

    async getAllMessages(conversationId, timeNext) {
        return await this._repository.getAllMessages(conversationId, timeNext);

    }

    async getAllConversations(userId, timeNext) {
        let conversations = await this._repository.getAllConversations(userId, timeNext);
        let results = [];
        for (let i = 0; i < conversations.length; i++) {
            let lastMessage = await this._repository.getLastMessage(conversations[i].id);
            if (lastMessage) {
                conversations[i].last_message = lastMessage;
                results.push(conversations[i]);
            }
        }
        return results;
    }
}