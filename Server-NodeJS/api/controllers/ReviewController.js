import BaseController from "./../../libs/hohoda/core/framework/BaseController";
import Container from "./../../libs/hohoda/core/Container";
import Utils from './../../libs/hohoda/util/Utils';

export default class ReviewController extends BaseController {

    constructor(req, res) {
        super(req, res);
    }

    addReview() {
        let userId = this.request.headers['user_id'];
        let reviewReq = this.request.body;
        let reviewService = Container.getService('ReviewService');
        reviewService.addNew(userId, reviewReq)
            .then(()=> {
                this.response.json({
                    status: 0,
                    messageCode: 200,
                    messageInfo: null,
                    body: null
                });
            })
            .catch(err=> {
                console.log(err);
                this.handleErrorException(err);
            })
    }

    getAllReview() {
        let userId = this.request.body.user_id;
        let timeNext = this.request.body.time_next;

        let reviewService = Container.getService('ReviewService');
        reviewService.getAllReview(userId, timeNext)
            .then(reviews=> {
                this.response.json({
                    status: 0,
                    messageCode: 200,
                    messageInfo: null,
                    body: reviews
                });
            })
            .catch(err=> {
                console.log(err);
                this.handleErrorException(err);
            });
    }
}