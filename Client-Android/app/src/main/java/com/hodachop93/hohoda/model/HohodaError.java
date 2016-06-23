package com.hodachop93.hohoda.model;

import com.hodachop93.hohoda.common.ErrorFactory;

public class HohodaError {

    public static final String CODE_EMPTY_RESPONSE_ERROR = "00001";
    public static final String MESSAGE_EMPTY_RESPONSE_ERROR = "RESPONSE_EMPTY_BODY";

    private String code;
    private String message;
    private String devInfo;
    private HohodaResponseBody responseBody;

    public HohodaError(String code) {
        this.code = code;
    }

    public HohodaError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public HohodaError(String code, String message, String devInfo) {
        this.code = code;
        this.message = message;
        this.devInfo = devInfo;
    }

    public static HohodaError newEmptyResponseError() {
        return new HohodaError(CODE_EMPTY_RESPONSE_ERROR, MESSAGE_EMPTY_RESPONSE_ERROR);
    }

    public static HohodaError newNoNetworkConnectionError() {
        return new HohodaError(ErrorFactory.CODE_NO_NETWORK_CONNECTION);
    }

    public static HohodaError newNetworkError(){
        return new HohodaError(ErrorFactory.CODE_NETWORK_ERROR);
    }

    public static HohodaError newInternalError() {
        return new HohodaError(ErrorFactory.CODE_INTERNAL_EXCEPTION);
    }

    public static HohodaError newConnectionTimeoutError() {
        return new HohodaError(ErrorFactory.CODE_REQUEST_TIMED_OUT);
    }

    public boolean isTimeoutError() {
        return ErrorFactory.CODE_REQUEST_TIMED_OUT.equals(code);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDevInfo() {
        return devInfo;
    }

    public void setDevInfo(String devInfo) {
        this.devInfo = devInfo;
    }

    public HohodaResponseBody getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(HohodaResponseBody responseBody) {
        this.responseBody = responseBody;
    }
}
