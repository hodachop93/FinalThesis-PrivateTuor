package com.hodachop93.hohoda.common;

public class ErrorFactory {

    // Un-handled Exception
    public static final String CODE_INTERNAL_EXCEPTION = "00000";
    public static final String CODE_NO_NETWORK_CONNECTION = "10001";
    public static final String CODE_REQUEST_TIMED_OUT = "10002";
    public static final String CODE_NETWORK_ERROR = "10003";

    public static final String CODE_USER_CREATED = "30001";
    public static final String CODE_USER_CREATED_NOT_ACTIVATED = "30002";
    public static final String CODE_USER_BLOCKED = "30003";
    public static final String CODE_USER_TERMINATED = "30004";
    public static final String CODE_USER_NOT_FOUND = "30005";
    public static final String CODE_INVALID_OTP = "30009";
    public static final String CODE_INVALID_USERNAME_PASSWORD = "30010";

    public static final String CODE_CANNOT_FETCH_JOBS = "50001";

    public static final String CODE_JOB_NOT_SAVED = "80001";

    public static final String CODE_INVALID_AUTHORIZATION_PAYPAL_CODE = "14001";
    public static final String CODE_CANNOT_CREATE_PAYMENT = "140002";

    public static final String CODE_SMS_SERVICE_CAN_NOT_SEND_MESSAGE = "150002";
    public static final String CODE_TOKEN_ID_NOT_FOUND = "150003";
    public static final String CODE_USER_ID_NOT_FOUND = "150004";

}
