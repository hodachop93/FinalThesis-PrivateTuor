export default class BaseRepository {
    constructor(db) {
        this._limit = 0;
        this._page = 1;
        this._db = db;
    }

    get limit() {
        return this._limit;
    }

    set limit(val) {
        this._limit = Math.max(parseInt(val, 10) || 0, 0);
    }

    get page() {
        return this._page;
    }

    set page(val) {
        this._page = Math.max(parseInt(val, 10) || 1, 1);
    }
}