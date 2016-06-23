import BaseService from "./BaseService";
import PopularhashtagRepository from "./../repositories/PopularhashtagRepository";
import UserException from "./../exceptions/UserException";
import {CAN_NOT_FETCH_ALL_POPULAR_HASHTAG, CAN_NOT_ADD_NEW_POPULAR_HASHTAG,
    CAN_NOT_REMOVE_POPULAR_HASHTAG, CAN_NOT_FIND_POPULAR_HASHTAG} from "./../util/ApplicationConstants";
import i18n from 'i18n';

export default class PopularhashtagService extends BaseService {

    constructor(popularhashtagRepository) {
        super();
        if (arguments.length) {
            this._repository = popularhashtagRepository;
        }else{
            this._repository = new PopularhashtagRepository();
        }
    }

    findAll(){
        return this._repository.findAll()
            .then(hashtags => {
                return hashtags;
            })
            .catch(err => {
                throw new UserException(i18n.__('msg_error_popular_hashtag_60001'), CAN_NOT_FETCH_ALL_POPULAR_HASHTAG, err.message, null);
            });
    }
}
