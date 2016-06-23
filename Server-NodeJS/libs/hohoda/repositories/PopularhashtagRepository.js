import * as objection from 'objection';
import BaseRepository from './BaseRepository';
import Popularhashtag from './../models/Popularhashtag';

export default class PopularhashtagRepository extends BaseRepository {
    constructor() {
        super();
    }

    findAll() {
        return Popularhashtag.query()
            .join("hashtag", "popular_hashtag.id", "hashtag.id")
            .orderBy('hashtag.created_at', 'desc');
    }
}