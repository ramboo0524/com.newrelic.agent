//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.harvest;

public class HarvestResponse {
    private static final String DISABLE_STRING = "DISABLE_NEW_RELIC";
    private int statusCode;
    private String responseBody;
    private long responseTime;

    public HarvestResponse() {
    }

    public HarvestResponse.Code getResponseCode() {
        if(this.isOK()) {
            return HarvestResponse.Code.OK;
        } else {
            HarvestResponse.Code[] var1 = HarvestResponse.Code.values();
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                HarvestResponse.Code code = var1[var3];
                if(code.getStatusCode() == this.statusCode) {
                    return code;
                }
            }

            return HarvestResponse.Code.UNKNOWN;
        }
    }

    public boolean isDisableCommand() {
        return HarvestResponse.Code.FORBIDDEN == this.getResponseCode() && DISABLE_STRING.equals(this.getResponseBody());
    }

    public boolean isError() {
        return this.statusCode >= 400;
    }

    public boolean isUnknown() {
        return this.getResponseCode() == HarvestResponse.Code.UNKNOWN;
    }

    public boolean isOK() {
        return !this.isError();
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getResponseBody() {
        return this.responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public long getResponseTime() {
        return this.responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }

    public enum Code {
        OK(200),
        UNAUTHORIZED(401),
        FORBIDDEN(403),
        ENTITY_TOO_LARGE(413),
        INVALID_AGENT_ID(450),
        UNSUPPORTED_MEDIA_TYPE(415),
        INTERNAL_SERVER_ERROR(500),
        UNKNOWN(-1);

        int statusCode;

        Code(int statusCode) {
            this.statusCode = statusCode;
        }

        public int getStatusCode() {
            return this.statusCode;
        }

        public boolean isError() {
            return this != OK;
        }

        public boolean isOK() {
            return !this.isError();
        }
    }
}
