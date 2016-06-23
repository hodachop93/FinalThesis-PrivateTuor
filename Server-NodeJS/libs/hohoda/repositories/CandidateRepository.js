import BaseRepository from "./BaseRepository";
import Candidate from "./../models/Candidate";
import Profile from './../models/Profile';
import Utils from "./../util/Utils";

export default class CandidateRepository extends BaseRepository {

    createCandidate(candidateReq) {
        candidateReq.id = Utils.generateUUID();
        candidateReq.created_at = Utils.generateTimestampLong();
        candidateReq.updated_at = Utils.generateTimestampLong();

        return Candidate.query().insert(candidateReq);
    }

    checkUserHasAlreadyJoined(userID, courseId) {
        return Candidate.query().where('user_id', userID).andWhere('course_id', courseId).first();
    }

    removeCandidate(userId, courseId) {
        return Candidate.query().delete()
            .where("user_id", userId)
            .andWhere("course_id", courseId);
    }

    getCandidateList(courseId) {
        return Candidate.query()
            .eager('[profile]')
            .pick(Profile, ['name', 'avatar_url', 'address', 'rate_average  '])
            .where('course_id', courseId);
    }
}
