export default class BaseService {

    constructor() {
        this._repository = null;
    }

    get limit() {
        if (this._repository != null)
            return this._repository.limit;
        return 0;
    }

    set limit(val) {
        if (this._repository != null)
            this._repository.limit = val;
    }

    get page() {
        if (this._repository != null)
            return this._repository.page;
        return 0;
    }

    set page(val) {
        if (this._repository != null)
            this._repository.page = val;
    }

    set setRepository(repository) {
        this._repository = repository;
    }
}