var config = {
    development: {
        mysqlConnection : {
            client: "mysql",
            connection: {
                host: "localhost",
                user: "root",
                password: "root",
                database: 'hohoda'
            }
        },
        gcm_api_key: "AIzaSyBAKILO-kUHn9zft2zhX2-2pesKFwGiOcQ",
        google_place_api_key: "AIzaSyBAKILO-kUHn9zft2zhX2-2pesKFwGiOcQ",
        redis: {
            port: 6379,
            host: 'localhost'
        }
    },

    test: {
        mysqlConnection : {
            client: "mysql",
            connection: {
                host: "localhost",
                user: "root",
                password: "root",
                database: 'hohoda'
            }
        },
        gcm_api_key: "AIzaSyBAKILO-kUHn9zft2zhX2-2pesKFwGiOcQ",
        google_place_api_key: "AIzaSyBAKILO-kUHn9zft2zhX2-2pesKFwGiOcQ",
        redis: {
            port: 6379,
            host: 'localhost'
        }
    },

    production: {
        mysqlConnection : {
            client: "mysql",
            connection: {
                host: "us-cdbr-iron-east-04.cleardb.net",
                user: "bd04f97b0ff503",
                password: "d3282254",
                database: 'heroku_881d5ddffdbb0a7'
            }
        },
        gcm_api_key: "AIzaSyBAKILO-kUHn9zft2zhX2-2pesKFwGiOcQ",
        google_place_api_key: "AIzaSyBAKILO-kUHn9zft2zhX2-2pesKFwGiOcQ",
        redis: {
            redis: process.env.REDIS_URL
        }
    }
};
module.exports = config;
