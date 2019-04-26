package com.netease.im;

public class ImPushConfig {

    /**
     * 小米推送 appId
     */
    public String xmAppId;

    /**
     * 小米推送 appKey
     */
    public String xmAppKey;

    /**
     * 小米推送证书，请在云信管理后台申请
     */
    public String xmCertificateName;

    /**
     * 华为推送 appId 请在 AndroidManifest.xml 文件中配置
     * 华为推送证书，请在云信管理后台申请
     */
    public String hwCertificateName;

    /**
     * 魅族推送 appId
     */
    public String mzAppId;

    /**
     * 魅族推送 appKey
     */
    public String mzAppKey;

    /**
     * 魅族推送证书，请在云信管理后台申请
     */
    public String mzCertificateName;

    /**
     * FCM 推送证书，请在云信管理后台申请
     * 海外客户使用
     */
    public String fcmCertificateName;

    public String getXmAppKey() {
        return xmAppKey;
    }

    public void setXmAppKey(String xmAppKey) {
        this.xmAppKey = xmAppKey;
    }

    public String getVivoCertificateName() {
        return vivoCertificateName;
    }

    public void setVivoCertificateName(String vivoCertificateName) {
        this.vivoCertificateName = vivoCertificateName;
    }

    public String getMzCertificateName() {
        return mzCertificateName;
    }

    public void setMzCertificateName(String mzCertificateName) {
        this.mzCertificateName = mzCertificateName;
    }

    public String getFcmCertificateName() {
        return fcmCertificateName;
    }

    public void setFcmCertificateName(String fcmCertificateName) {
        this.fcmCertificateName = fcmCertificateName;
    }

    public String getMzAppKey() {
        return mzAppKey;
    }

    public void setMzAppKey(String mzAppKey) {
        this.mzAppKey = mzAppKey;
    }

    public String getMzAppId() {
        return mzAppId;
    }

    public void setMzAppId(String mzAppId) {
        this.mzAppId = mzAppId;
    }

    public String getHwCertificateName() {
        return hwCertificateName;
    }

    public void setHwCertificateName(String hwCertificateName) {
        this.hwCertificateName = hwCertificateName;
    }

    public String getXmCertificateName() {
        return xmCertificateName;
    }

    public void setXmCertificateName(String xmCertificateName) {
        this.xmCertificateName = xmCertificateName;
    }

    /**
     * VIVO推送 appId apiKey请在 AndroidManifest.xml 文件中配置
     * VIVO推送证书，请在云信管理后台申请
     */
    public String vivoCertificateName;

    public String getXmAppId() {
        return xmAppId;
    }

    public void setXmAppId(String xmAppId) {
        this.xmAppId = xmAppId;
    }

}
