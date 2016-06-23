import BaseController from "./../../libs/hohoda/core/framework/BaseController";
import Container from "./../../libs/hohoda/core/Container";

export default class MessageController extends BaseController {

    constructor(req, res) {
        super(req, res);
    }

    addNew() {
        let userId = this.request.headers['user_id'];
        let addMessReq = this.request.body;

        let messageService = Container.getService('MessageService');
        messageService.addNew(userId, addMessReq)
            .then(message => {
                this.response.json({
                    status: 0,
                    messageCode: 200,
                    messageInfo: null,
                    body: message
                });
            })
            .catch(err => {
                console.log(err);
                this.handleErrorException(err);
            });
    }

    startChat() {
        let fromId = this.request.headers['user_id'];
        let toId = this.request.body.to_id;
        let messageService = Container.getService('MessageService');
        messageService.startConversation(fromId, toId)
            .then(conversation => {
                this.response.json({
                    status: 0,
                    messageCode: 200,
                    messageInfo: null,
                    body: conversation
                });
            })
            .catch(err => {
                console.log(err);
                this.handleErrorException(err);
            });
    }

    getAllMessages() {
        let userId = this.request.headers['user_id'];
        let conversationId = this.request.body.conversation_id;
        let timeNext = this.request.body.time_next;

        let messageService = Container.getService('MessageService');
        messageService.getAllMessages(conversationId, timeNext)
            .then(messages => {
                this.response.json({
                    status: 0,
                    messageCode: 200,
                    messageInfo: null,
                    body: messages
                });
            })
            .catch(err => {
                console.log(err);
                this.handleErrorException(err);
            });

    }

    getAllConversations() {
        let userId = this.request.headers['user_id'];
        let timeNext = this.request.body.time_next;
        let messageService = Container.getService('MessageService');

        messageService.getAllConversations(userId, timeNext)
            .then(conversations => {
                this.response.json({
                    status: 0,
                    messageCode: 200,
                    messageInfo: null,
                    body: conversations
                });
            })
            .catch(err => {
                console.log(err);
                this.handleErrorException(err);
            });
    }
}