import BaseController from "./../../libs/hohoda/core/framework/BaseController";
import Container from "./../../libs/hohoda/core/Container";

export default class TutorController extends BaseController {
    async getTutorByUserInterest() {
        let reqBody = this.request.body;
        let userId = this.request.headers['user_id'];
        let tutorService = Container.getService('TutorService');
        try {
            let tutorList = await tutorService.getTutorByUserInterest(userId, reqBody);
            this.response.json({
                status: 0,
                messageCode: 200,
                messageInfo: null,
                body: tutorList
            });
        } catch (err) {
            this.handleErrorException(err);
        }
    }

    async getTutorByUserLocal() {
        let reqBody = this.request.body;
        let userId = this.request.headers['user_id'];
        let tutorService = Container.getService('TutorService');
        try {
            let tutorList = await tutorService.getTutorByUserLocal(userId, reqBody);
            this.response.json({
                status: 0,
                messageCode: 200,
                messageInfo: null,
                body: tutorList
            });
        } catch (err) {
            this.handleErrorException(err);
        }
    }

    searchTutorsByHashtag() {
        let userId = this.request.headers['user_id'];
        let hashtags = this.request.body.hashtags;
        let timeNext = this.request.body.time_next;

        let tutorService = Container.getService('TutorService');
        tutorService.searchTutorsByHashtag(userId, hashtags, timeNext)
            .then(tutors => {
                this.response.json({
                    status: 0,
                    messageCode: 200,
                    messageInfo: null,
                    body: tutors
                });
            })
            .catch(err => {
                console.log(err);
                this.handleErrorException(err);
            })
    }
}