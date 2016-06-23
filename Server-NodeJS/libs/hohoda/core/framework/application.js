import path from "path";
import fs from "fs";
import express from "express";
import bodyParser from "body-parser";
import cookieParser from "cookie-parser";
import exphbs from "express-handlebars";
import JWT from "jsonwebtoken";
import i18n from "i18n";

export default function (basePath) {
    let app = express();
    app.set('basePath', basePath);

    let viewPath = path.join(basePath, "views");
    app.set("views", viewPath);
    app.engine('.hbs', exphbs({
        extname: '.hbs',
        layoutsDir: path.resolve(basePath, path.basename(viewPath) + "/layouts")
    }));
    app.set('view engine', '.hbs');
    //hbs.registerPartial('header', fs.readFileSync(basePath + '/../web/views/partials/header.hbs', 'utf8'));
    app.enable("trust proxy");
    app.enable("json spaces", 4);
    app.use(bodyParser.json({limit: '50mb'}));
    app.use(bodyParser.urlencoded({limit: '50mb', extended: true}));
    app.use(cookieParser());

    i18n.configure({
        locales: ['en', 'vi'],
        directory: __dirname + "/../../locales"

    });

    // load routes
    let routingFile = path.join(basePath, ...["configs", "routes.js"]);
    if (fs.existsSync(routingFile)) {
        require(routingFile)(app);
        app.use(function (request, response, next) {
            if (request.headers['accept-language']){
                i18n.setLocale(request.headers['accept-language']);
            }else{
                i18n.setLocale('en');
            }
            next();
        })
    }

    app.use(express.static(basePath + "/public"));
    app.use('/view', express.static(basePath + "/views"));
    app.use('/photos', express.static(basePath + '/../uploads'));
    app.use(i18n.init);
    return app;

}