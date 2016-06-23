import kue from 'kue';
import config from "./../../../configs/config";

let queue = kue.createQueue(config[process.env.NODE_ENV].redis);

//job file list for Kue to execute
const JOBS = ['PushnotiwhenpostcourseJob', 'PushnotiwhenjoincourseJob', 'PushnotiwhenchatJob'];

//Port Kue
const PORT_KUE = 3005;

for (let jobName of JOBS) {
    require(__dirname + '/jobs/' + jobName)(queue);
}


queue.on('job enqueue', function (id, type) {
    console.log('Job %s got queued of type %s', id, type);

}).on('job complete', function (id, result) {
    console.log('Job %s completed', id);
});

kue.app.listen(PORT_KUE);

export default queue;