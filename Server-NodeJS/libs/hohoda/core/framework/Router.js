import path from "path";
import pathToRegexp from "path-to-regexp";
import fs from "fs";
import System from "systemjs";
var parse = require('url').parse;

export default class Router {
    constructor(app) {
        this.routes = [];

        // private variables
        this._basePath = app.get("basePath");
        this._compiledRoutes = {};
        this._namespaces = [];
        this._path = [];
        this._dispatch = null;
    }

    connect(path, options = {}) {
        let namespaces = this._namespaces;
        let fullPath = this._path;

        // remove slashes at the start and end
        path = path.replace(/^\/|\/$/g, '');

        let verb = options.method || 'all';
        let action = options.action || "index";
        let controllerName = options.controller;
        if (!controllerName) {
            let parts = path.split("/");
            controllerName = parts[0];
        }

        // insert path component at the end
        fullPath.push(path);

        let target = {
            controller: controllerName,
            action: action,
            namespace: null
        };
        // if there are namespaces append them to the target
        if (namespaces.length) {
            let namespace = namespaces.join('/');
            target.namespace = namespace;
        }

        // build a path and prepend a slash
        path = `/${ fullPath.join('/') }`;
        let route = {verb, path, target};

        // add route to the collection
        this.routes.push(route);

        // after route was added
        // remove its component from path
        fullPath.length--;
    }

    namespace(path, routes) {
        let namespaces = this._namespaces;
        let fullPath = this._path;

        // remove slashes at the start and end
        path = path.replace(/^\/|\/$/g, '');

        // add path component
        fullPath.push(path);

        // keeping track of all namespaces
        namespaces.push(path);

        routes.call(this);

        // remove namespace name from path
        fullPath.pop();

        // namespace is no longer needed, remove
        namespaces.length--;
    }

    resolve(method, url) {
        method = method.toLowerCase();

        let routes = this._compiledRoutes[method];
        if (!routes)
            return null;
        let route;
        let index = 0;

        url = parse(url).pathname;

        while (route = routes[index++]) {
            let params;

            if (params = route.regexp.exec(url)) {
                // extract parameters from .exec() result
                // and decodeURIComponent each of them
                params = params.slice(1).map(decodeURIComponent);

                // assign keys to params array
                route.keys.forEach((key, i) => params[key] = params[i]);
                route.params = params;

                return route;
            }
        }
    }

    initialize() {
        this.routes.forEach(route => {
            let namespace = route.target.namespace;
            let controller = route.target.controller;
            let action = route.target.action;

            let path = route.path;

            let keys = [];
            let regexp = pathToRegexp(route.path, keys);
            keys = keys.map(key => key.name);
            if (typeof this._compiledRoutes[route.verb] === 'undefined')
                this._compiledRoutes[route.verb] = [];
            this._compiledRoutes[route.verb].push({
                path,
                regexp,
                keys,
                namespace,
                controller,
                action
            });
        });
        let router = this;
        return function (request, response, next) {
            let route = router.resolve(request.method, request.url);

            if (!route) {
                route = router.resolve('all', request.url);
            }
            if (!route) {
                request.status = 404;
                next(false);
                return;
            }
            request.route = route;
            request.params = route.params;
            let Controller = router.getController(route.controller);
            if (Controller) {
                let controllerObj = new Controller(request, response);
                controllerObj.next = next;
                controllerObj[route.action].call(controllerObj, next);
            } else {
                next();
            }
        };
    }

    getController(controllerAlias) {
        let controllerName = this.getControllerName(controllerAlias) + ".js";
        let controllerPath = path.join(this._basePath, ...["controllers", controllerName]);
        let exportController = require(controllerPath);
        if (exportController.default) {
            return exportController.default;
        }
        return exportController;
    }

    getControllerName(name) {
        return name.charAt(0).toUpperCase() + name.slice(1).toLowerCase() + "Controller";
    }
}