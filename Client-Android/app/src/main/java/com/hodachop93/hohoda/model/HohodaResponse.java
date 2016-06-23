package com.hodachop93.hohoda.model;

import java.io.Serializable;
import java.util.List;

public class HohodaResponse<T> implements Serializable {

    private String status;
    private String messageCode;
    private String messageInfo;
    private String devInfo;
    private T body;

    public String getStatus() {
        return status;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public String getMessageInfo() {
        return messageInfo;
    }

    public String getDevInfo() {
        return devInfo;
    }

    public T getBody() {
        return body;
    }

    public boolean isSuccess() {
        return status != null && status.equals("0");
    }

    public HohodaError getError() {
        HohodaError error = new HohodaError(messageCode, messageInfo);
        error.setResponseBody((HohodaResponseBody) body);
        return error;
    }
}
