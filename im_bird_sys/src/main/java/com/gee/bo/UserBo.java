package com.gee.bo;

public class UserBo {
    private String userId;
    private String faceData;

    public void setFaceData(String faceData) {
        this.faceData = faceData;
    }

    public String getFaceData() {
        return faceData;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "UserBo{" +
                "userId='" + userId + '\'' +
                ", faceData='" + faceData + '\'' +
                '}';
    }
}
