import path from "path";

class Container {

    constructor() {
        this.modules = {};
    }

    getService(alias) {
        if (!this.modules[alias]) {
            let servicePath = __dirname + "/../services/" + alias;
            let exportService = require(servicePath);
            if (exportService.default) {
                exportService = exportService.default;
            }

            let ins = new exportService();
            this.modules[alias] = ins;
        }
        return this.modules[alias];
    }
}

let _instance = new Container();
export default _instance;