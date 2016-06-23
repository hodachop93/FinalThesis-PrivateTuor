import BaseRepository from "./BaseRepository";
import Message from './../models/Message';
import Conversation from './../models/Conversation';
import Profile from './../models/Profile';
import Utils from "./../util/Utils";
import {MAX_ITEMS_PER_LOAD, MAX_MESSAGES_PER_LOAD} from "./../util/ApplicationConstants";

export default class MessageRepository extends BaseRepository {

    constructor() {
        super();
    }

    async addNew(userId, addMessReq) {
        let conversationId = addMessReq.conversation_id;
        let content = addMessReq.content;
        let createdAt = addMessReq.created_at;

        if (conversationId) {
            let data = {
                id: Utils.generateUUID(),
                conversation_id: conversationId,
                user_id: userId,
                content: content,
                created_at: createdAt,
                updated_at: Utils.generateTimestampLong()
            };
            let message = await Message.query().insert(data);
            if (message) {
                await this.updateConversationWhenHaveNewMessage(conversationId);
            }
            return message;
        }
    }

    async startConversation(memberOne, memberTwo) {
        let conversation = await this.checkConversationExisted(memberOne, memberTwo);

        if (!conversation) {
            conversation = await this.addNewConversation(memberOne, memberTwo);
        }
        return await this.getConversation(conversation.id);
    }

    getAllMessages(conversationId, timeNext) {
        return Message.query()
            .where('conversation_id', conversationId)
            .andWhere('created_at', '<', timeNext)
            .orderBy('created_at', 'desc')
            .limit(MAX_MESSAGES_PER_LOAD);
    }

    getConversation(conversationId) {
        return Conversation.query()
            .eager('[person_one, person_two]')
            .pick(Profile, ['user_id', 'name', 'avatar_url'])
            .where('id', conversationId)
            .first();
    }

    getAllConversations(userId, timeNext) {
        return Conversation.query()
            .eager('[person_one, person_two]')
            .pick(Profile, ['user_id', 'name', 'avatar_url'])
            .where(function () {
                this.where('member_one', userId).orWhere('member_two', userId)
            })
            .andWhere("created_at", "<", timeNext)
            .orderBy('created_at', 'desc')
            .limit(MAX_ITEMS_PER_LOAD);
    }


    addNewConversation(memberOne, memberTwo) {
        let data = {
            id: Utils.generateUUID(),
            member_one: memberOne,
            member_two: memberTwo,
            created_at: Utils.generateTimestampLong(),
            updated_at: Utils.generateTimestampLong()
        };

        return Conversation.query()
            .insert(data);
    }

    checkConversationExisted(memberOne, memberTwo) {
        return Conversation.query()
            .where(function () {
                this.where('member_one', memberOne).andWhere('member_two', memberTwo);
            })
            .orWhere(function () {
                this.where('member_one', memberTwo).andWhere('member_two', memberOne);
            })
            .first();
    }

    updateConversationWhenHaveNewMessage(conversationId) {
        return Conversation.query().patch({updated_at: Utils.generateTimestampLong()}).where('id', conversationId);
    }

    getLastMessage(conversationId) {
        return Message.query()
            .pluck('content')
            .where('conversation_id', conversationId)
            .orderBy('created_at', 'desc')
            .first();
    }
}