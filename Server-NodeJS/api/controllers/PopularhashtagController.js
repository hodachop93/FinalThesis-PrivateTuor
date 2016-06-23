import BaseController from "./../../libs/hohoda/core/framework/BaseController";
import Container from "./../../libs/hohoda/core/Container";

export default class PopularhashtagController extends BaseController {

    constructor(req, res) {
        super(req, res);
    }

    findAll(){
        let popularHashtagService = Container.getService('PopularhashtagService');
        popularHashtagService.findAll()
            .then(hashtags => {
                this.response.json({
                    status: 0,
                    messageCode: 200,
                    messageInfo: null,
                    body: hashtags
                })
            })
            .catch(err => {
                this.handleErrorException(err);
            })
    }
}