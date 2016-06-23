import Router from "./../../libs/hohoda/core/framework/AppRouter";

module.exports = function (app) {
    let router = new Router(app);

    // ------------- Un-Authenticated ----------------------
    router.connect("/api/hashtag/popular", {
        method: "get",
        controller: "popularhashtag",
        action: "findAll"
    });

    router.connect("/api/user/registration", {
        method: "post",
        controller: "registrationuser",
        action: "register"
    });
    router.connect('/api/user/activate', {
        method: 'post',
        controller: 'Activationuser',
        action: 'activateUser'
    });
    router.connect('/api/user/signin', {
        method: 'post',
        controller: 'signin',
        action: 'signin'
    });

    router.connect('/api/course/searchByHashtag', {
        method: 'post',
        controller: 'searchcoursebyhashtag',
        action: 'searchCourseByHashtag'
    });
    router.connect('/api/course/searchByLocation', {
        method: 'post',
        controller: 'course',
        action: 'searchCourseByLocation'
    });
    router.connect('/api/user/loginsocialnetwork', {
        method: 'post',
        controller: 'signin',
        action: 'loginSocialNetwork'
    });

    // ----------------- Authenticated ---------------------------
    router.group(['auth'], {
        //TODO: Here
        '/api/secure/course/post': {
            method: 'post',
            controller: 'postcourse',
            action: 'postCourse'
        },
        '/api/secure/course/join': {
            method: 'post',
            controller: 'joincourse',
            action: 'joinCourse'
        },
        '/api/secure/course/unjoin': {
            method: 'post',
            controller: 'course',
            action: 'unJoinCourse'
        },
        '/api/secure/course/close': {
            method: 'post',
            controller: 'course',
            action: 'closeCourse'
        },
        "/api/secure/course/recommend": {
            method: 'post',
            controller: "recommendcourse",
            action: "getRecommendCourses"
        },
        '/api/secure/mycourse/tutor': {
            method: 'post',
            controller: 'course',
            action: 'getMyCourseAsTutor'
        },
        '/api/secure/mycourse/student': {
            method: 'post',
            controller: 'course',
            action: 'getMyCourseAsStudent'
        },
        '/api/secure/course/latest': {
            method: 'post',
            controller: 'course',
            action: 'getLatestCourse'
        },
        '/api/secure/course/local': {
            method: 'post',
            controller: 'course',
            action: 'getLocalCourse'
        },
        '/api/secure/course/detail': {
            method: 'post',
            controller: 'course',
            action: 'getCourseDetail'
        },
        '/api/secure/course/getCandidateList': {
            method: 'post',
            controller: 'course',
            action: 'getCandidateList'
        },
        "/api/secure/user/profile": {
            method: 'post',
            controller: "userprofile",
            action: "getUserProfile"
        },
        "/api/secure/user/updateProfile": {
            method: 'post',
            controller: 'userprofile',
            action: "updateUserProfile"
        },
        '/api/secure/user/signout': {
            method: 'get',
            controller: 'signout',
            action: 'signOut'
        },
        '/api/secure/tutor/myinterest': {
            method: 'post',
            controller: 'tutor',
            action: 'getTutorByUserInterest'
        },
        '/api/secure/tutor/mylocal': {
            method: 'post',
            controller: 'tutor',
            action: 'getTutorByUserLocal'
        },
        '/api/secure/tutor/searchByHashtag': {
            method: 'post',
            controller: 'tutor',
            action: 'searchTutorsByHashtag'
        },
        '/api/secure/user/saveDeviceToken': {
            method: 'get',
            controller: 'devicetoken',
            action: 'saveDeviceToken'
        },
        '/api/secure/message/addNew': {
            method: 'post',
            controller: 'message',
            action: 'addNew'
        },
        '/api/secure/message/startChat': {
            method: 'post',
            controller: 'message',
            action: 'startChat'
        },
        '/api/secure/message/getAllConversations': {
            method: 'post',
            controller: 'message',
            action: 'getAllConversations'
        },
        '/api/secure/message/getAllMessages': {
            method: 'post',
            controller: 'message',
            action: 'getAllMessages'
        },
        '/api/secure/notification/getAllNotifications': {
            method: 'post',
            controller: 'notification',
            action: 'getAllNotifications'
        },
        '/api/secure/notification/updateStatus': {
            method: 'post',
            controller: 'notification',
            action: 'updateNotificationStatus'
        },
        '/api/secure/review/add': {
            method: 'post',
            controller: 'review',
            action: 'addReview'
        },
        '/api/secure/user/getAllReview': {
            method: 'post',
            controller: 'review',
            action: 'getAllReview'
        }
    });

    router.initialize();
    return router;
};