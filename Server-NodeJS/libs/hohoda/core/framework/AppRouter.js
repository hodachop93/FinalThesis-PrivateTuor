import path from "path";
import pathToRegexp from "path-to-regexp";
import fs from "fs";
import _ from 'lodash';

var flash = require("connect-flash");

export default class AppRouter {
  constructor(app) {
    this.app = app;
    this.middlewares = [];
    this.routes = {};
    this._basePath = app.get("basePath");
  }
  globalMiddlewares(middlewares) {
    let self = this;
    if (_.isString(middlewares)) {
      self.middlewares.push(middlewares);
    } else if (_.isArray(middlewares)) {
      let flattenMiddlewares = _.flatten(middlewares);
      _.forEach(flattenMiddlewares, function(val, key) {
        if (_.isString(val)) {
          self.middlewares.push(val);
        } else {
          throw new Error('Invalid middlewares');
          return;
        }
      });
    } else {
      throw new Error('Invalid Middlewares');
      return;
    }
  }
  group(middlewares, routes) {
    if (!_.isArray(middlewares) || !_.isObject(routes)) {
      throw new Error('Invalid routing.');
      return;
    }
    middlewares = _.flatten(middlewares);
    _.forEach(middlewares, function(val, key) {
      if (!_.isString(val)) {
        throw new Error('Invalid routing.');
        return;
      }
    });
    let self = this;
    _.forEach(routes, function(val, key) {
      if (!_.isString(key)) {
        throw new Error('Invalid routing.');
        return;
      }
      if (!val.middlewares)
        val.middlewares = [];
      if (!_.isArray(val.middlewares)) {
        throw new Error('Invalid routing.');
        return;
      }
      val.middlewares = _.concat(middlewares, val.middlewares);
      self.connect(key, val);
    });
  }
  connect(path, options = {}) {
    // convert middlewares to flatten array
    if (!options.middlewares || !_.isArray(options.middlewares))
      options.middlewares = [];
    options.middlewares = _.flatten(options.middlewares);
    let opts = _.merge({
      controller: 'home',
      action: 'index',
      middlewares: [],
      method: 'all'
    }, options);

    path = (path) ? path : '/';
    if (this.routes[path]) {
      opts.middlewares = _.concat(this.routes[path].middlewares, opts.middlewares);
    }
    this.routes[path] = opts;
  }
  initialize() {
    let self = this;
    self.middlewares = _.uniq(self.middlewares);
    let globalMiddlewares = [];
    let loadedMiddlewares = {};
    // load all global midleware
    _.forEach(self.middlewares, function(val, key) {
      if (typeof loadedMiddlewares[val] !== 'function') {
        loadedMiddlewares[val] = function(req, res, next) {
          let Filter = self.__getMiddleware(val);
          let filter = new Filter();
          return filter.handle(req, res, next);
        };
        globalMiddlewares.push(loadedMiddlewares[val]);
      }
    });
    // add routes to the app
    _.forEach(self.routes, function(opts, path) {
      let method = opts.method || 'all';
      //load global middlewares
      let actionMiddlwares = [];
      // load middlewares for the action
      _.forEach(_.uniq(opts.middlewares), function(val, key) {
        if (typeof loadedMiddlewares[val] === 'undefined') {
          loadedMiddlewares[val] = function(req, res, next) {
            let Filter = self.__getMiddleware(val);
            let filter = new Filter();
            return filter.handle(req, res, next);
          };
        }
        actionMiddlwares.push(loadedMiddlewares[val]);
      });
      let applyMiddlewares = _.concat(globalMiddlewares, actionMiddlwares);
      self.app[method].call(self.app, path, applyMiddlewares, function(req, res, next) {
        let controllerObj = null;
        let controllerClass = self.__getController(opts.controller);
        if (controllerClass) {
          controllerObj = new controllerClass(req, res);
          controllerObj.next = next;
        }
        if (controllerObj != null) {
          controllerObj[opts.action].call(controllerObj, next);
        } else {
          next(false);
        }
      });
    });
  }
  __getMiddleware(name) {
    let middlewareName = _.lowerCase(name) + ".js";
    let middlewarePath = path.join(this._basePath, ...["middlewares", middlewareName]);
    let exportMiddleware = require(middlewarePath);
    if (exportMiddleware.default) {
      return exportMiddleware.default;
    }
    return exportMiddleware;
  }
  __getController(controllerAlias) {
    let controllerName = _.upperFirst(controllerAlias) + "Controller.js";
    let controllerPath = path.join(this._basePath, ...["controllers", controllerName]);
    let webAPIcontrollerPath = path.join(this._basePath, ...["api", "controllers", controllerName]);
    let exportController;

    if (fs.existsSync(controllerPath)) {
      exportController = require(controllerPath);
    } else if (fs.existsSync(webAPIcontrollerPath)) {
      exportController = require(webAPIcontrollerPath);
    }

    if (exportController.default) {
      return exportController.default;
    }
    return exportController;
  }
}