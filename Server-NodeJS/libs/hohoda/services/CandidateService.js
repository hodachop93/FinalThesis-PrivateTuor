import BaseService from "./BaseService";
import UserException from "./../exceptions/UserException";
import CandidateRepository from './../repositories/CandidateRepository';
import i18n from "i18n";

export default class CandidateService extends BaseService{

    constructor(candidateRepository) {
        super();
        if(arguments.length){
            this._repository = candidateRepository
        }else{
            this._repository = new CandidateRepository();
        }
    }

    checkUserHasAlreadyJoined(userID, courseId) {
        return this._repository.checkUserHasAlreadyJoined(userID, courseId);
    }

    removeCandidate(userId, courseId){
        return this._repository.removeCandidate(userId, courseId);
    }
}