import dbConfig from "./../configs/config";
import Knex from "knex";
import {Model} from "objection";
import mogan from "morgan";

module.exports = function (app){
    // setting database connection
    let env = app.get('env');
    var knex = Knex(dbConfig[env].mysqlConnection);
    Model.knex(knex);
    app.set("knex", knex);
}