import constants from 'node-constants';
let define = constants.definer(exports);

define("MAX_ACTIVE_JOBS", 20);
define('HOHODA_APPLICATION_NAME', 'Hohoda Application');

// User Error
define("USER_EXISTED", 30001);
define("USER_NOT_ACTIVATE", 30002);
define("USER_BLOCKED", 30003);
define("USER_DESTROYED", 30004);
define("USER_NOT_FOUND", 30005);
define("THE_USER_NAME_OR_EMAIL_HAS_ALREADY_USED", 30006);
define("CAN_NOT_FETCH_PHONE_NUMBER", 30007);
define("CAN_NOT_FETCH_USERNAME", 30008);
define("USER_CANT_NOT_ACTIVATE_BY_OTP_CODE", 30009);
define("USER_CANT_NOT_VERIFY_BY_PASSWORD", 30010);

// Profile Error
define("CAN_NOT_UPDATE_PROFILE", 40001);
define("CAN_NOT_FETCH_PROFILE", 40002);

// Hashtags Error
define("CAN_NOT_FETCH_COURSES", 50001);
define("CAN_NOT_FETCH_DETAIL_COURSE", 50002);

// Popular Hashtags Error
define("CAN_NOT_FETCH_ALL_POPULAR_HASHTAG", 60001);
define("CAN_NOT_ADD_NEW_POPULAR_HASHTAG", 60002);
define("CAN_NOT_REMOVE_POPULAR_HASHTAG", 60003);
define("CAN_NOT_FIND_POPULAR_HASHTAG", 60004);

// Candidate Error
define("CAN_NOT_FETCH_ALL_CANDIDATE", 70001);
define("COURSE_WAS_CLOSED", 70002);
define("CAN_NOT_JOIN_COURSE", 70003);
define("CAN_NOT_REJECT_A_CANDIDATE", 70004);
define("CAN_NOT_CANCEL_A_COURSELISTING", 70005);
define("HAVE_ALREADY_JOINED_COURSE", 70006);

// Course Error
define("CAN_NOT_POST_A_COURSE", 80001);
define("COURSE_NOT_FOUND", 80002);
define("COURSE_NOT_DONE", 80003);

// User Setting Error
define("CAN_NOT_GET_SETTING_USER", 90001);
define("CAN_NOT_UPDATE_SETTING_USER", 90002);

// User Review Error
define("CAN_NOT_FETCH_ALL_REVIEW", 100001);
define("CAN_NOT_CREATE_REVIEW", 100002);
define("NOT_ALLOWED_TO_CREATE_REVIEW", 100003);
define("PROFILE_NOT_EXISTED", 100004);
define("PROFILE_DESTROYED", 100005);

// User Login Device Error
define("CAN_NOT_ADD_NEW_LOGIN_DEVICE", 110001);

// Notification Error
define("CAN_NOT_SEND_NOTIFICATION", 120001);

// Message Error
define("CAN_NOT_GET_INBOX_MESSAGE", 130001);
define("CAN_NOT_SEND_MESSAGE", 130002);
define("CAN_NOT_DELETE_MESSAGE", 130003);
define("CAN_NOT_RESEND_MESSAGE", 130004);

// System Error (>= 150000)
define("INTERNAL_SERVER_ERROR", 150001);
define("EMAIL_SERVER_ERROR", 150002);
define("TOKEN_ID_NOT_FOUND", 150003);
define("USER_ID_NOT_FOUND", 150004);

//Common Constants (always on the bottom)
define("MAX_ITEMS_PER_LOAD", 10);
define('MAX_MESSAGES_PER_LOAD', 30);
define("COURSE_CLOSED", 2);
define("OTP_EXPIRE_DATE", 24 * 60 * 60);
define("TOKEN_EXPIRE_DATE", 365 * 24 * 60 * 60);
define("TOKEN_SECURE_KEY", "superSecret");

//User Status Constants
define("USER_STATUS_NOT_ACTIVATED", 0);
define("USER_STATUS_ACTIVATED", 1);
define("USER_STATUS_BLOCKED", 2);
define("USER_STATUS_DESTROYED", 3);

//Course status error
define("COURSE_STATUS_DRAFT", 0);
define("COURSE_STATUS_POSTED", 1);
define("COURSE_STATUS_CLOSED", 2);
define("COURSE_STATUS_PENDING", 3);

//Notification status
define("NOTIFICATION_STATUS_UNREAD", 0);
define("NOTIFICATION_STATUS_READ", 1);

//Push Notification type
define("PUSH_NOTI_WHEN_POST_COURSE_TYPE", 0);
define('PUSH_NOTI_WHEN_JOIN_COURSE_TYPE', 1);
define('PUSH_NOTI_WHEN_CLOSE_COURSE_TYPE', 2);
define('PUSH_NOTI_WHEN_CHAT_TYPE', 10);

//Type job name for Kue
define("PUSH_NOTI_WHEN_POST_COURSE_KUE", "push noti when post a job");
define('PUSH_NOTI_WHEN_JOIN_COURSE_KUE', "push noti when join a course");
define('PUSH_NOTI_WHEN_CHAT_KUE', "push noti when chat");
define('PUSH_NOTI_WHEN_CLOSE_COURSE_KUE', "push noti when close a course");



