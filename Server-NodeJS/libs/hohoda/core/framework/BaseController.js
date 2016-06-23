import path from "path";
import _ from "lodash";

export default class BaseController {
    constructor(req, res) {
        this.layout = "layout";
        this.request = req;
        this.response = res;
        this._pageData = {};
        this._appData = {};
    }

    setAppData(key, val) {
        this._appData[key] = val;
    }

    getAppData(key) {
        return this._appData[key];
    }

    setData(key, val) {
        this._pageData[key] = val;
    }

    getData(key, val) {
        return this._pageData[key];
    }

    getParam(paramName) {
        return this.request.params[paramName];
    }

    render(viewName = "", data = {}) {
        let parts = viewName.split("/");
        if (parts.length == 1) {
            if (viewName.length == 0) {
                viewName = this.request.route.action;
            }
            if (this.request.route.controller) {
                viewName = path.join(this.request.route.controller, viewName);
            }
        }
        let viewData = _.merge(this._pageData, data, this._appData);
        viewData.layout = this.layout;
        this.response.render(viewName, viewData);
    }

    handleErrorException(err) {
        if (err.data) {
            this.response.json({
                status: 1,
                messageCode: err.errorCode,
                messageInfo: err.message,
                devInfo: err.errorDevMessage,
                body: {
                    user_id: err.data.id
                }
            });
        } else {
            this.response.json({
                status: 1,
                messageCode: err.errorCode,
                messageInfo: err.message,
                devInfo: err.errorDevMessage,
                body: null
            });
        }
    }
}

