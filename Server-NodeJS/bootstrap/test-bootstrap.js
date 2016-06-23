process.env.NODE_ENV = 'development';

import dbConfig from "./../configs/config";
import Knex from "knex";
import {Model} from "objection";
import mogan from "morgan";

// setting database connection
let env = 'development';
var knex = Knex(dbConfig[env].mysqlConnection);
Model.knex(knex);

module.exports = knex;
