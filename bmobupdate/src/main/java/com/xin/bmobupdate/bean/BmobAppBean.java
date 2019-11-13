package com.xin.bmobupdate.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017/12/27.
 */

public class BmobAppBean implements Parcelable {

    /**
     * channel : com.zstart.jiajiashop.debug
     * createdAt : 2017-12-27 10:22:47
     * objectId : 1148a213d5
     * path : {"__type":"File","cdn":"upyun","filename":"1514341347900.apk","group":"upyun","url":"http://bmob-cdn-15354.b0.upaiyun.com/2017/12/27/fdbc7f1540143e8880169cfa799fbbbf.apk"}
     * platform : Android
     * target_size : 12936092
     * update_log : log
     * updatedAt : 2017-12-27 11:07:52
     * version : 1.2
     * version_i : 10
     */

    private String channel;
    private String createdAt;
    private String objectId;
    private PathBean path;
    private String platform;
    private Long target_size;
    private String update_log;
    private String updatedAt;
    private String version;
    private String android_url;
    private int version_i;
    private boolean isforce;
    public BmobAppBean() {
    }

    /**
     * 下载地址是否有效的
     *
     * @return
     */
    public boolean isValidDownUrl() {
        if (path != null && path.getUrl() != null) {
            return true;
        }
        if (android_url != null && android_url.length() > 1) {
            return true;
        }
        return false;
    }

    public void setTarget_size(Long target_size) {
        this.target_size = target_size;
    }

    public String getAndroid_url() {
        return android_url;
    }

    public void setAndroid_url(String android_url) {
        this.android_url = android_url;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public PathBean getPath() {
        return path;
    }

    public void setPath(PathBean path) {
        this.path = path;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public Long getTarget_size() {
        return target_size;
    }

    public boolean isIsforce() {
        return isforce;
    }

    public void setIsforce(boolean isforce) {
        this.isforce = isforce;
    }

    public void setTarget_size(long target_size) {
        this.target_size = target_size;
    }

    public String getUpdate_log() {
        return update_log;
    }

    public void setUpdate_log(String update_log) {
        this.update_log = update_log;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getVersion_i() {
        return version_i;
    }

    public void setVersion_i(int version_i) {
        this.version_i = version_i;
    }

    public static class PathBean implements Parcelable {
        public static final Creator<PathBean> CREATOR = new Creator<PathBean>() {
            @Override
            public PathBean createFromParcel(Parcel source) {
                return new PathBean(source);
            }

            @Override
            public PathBean[] newArray(int size) {
                return new PathBean[size];
            }
        };
        /**
         * __type : File
         * cdn : upyun
         * filename : 1514341347900.apk
         * group : upyun
         * url : http://bmob-cdn-15354.b0.upaiyun.com/2017/12/27/fdbc7f1540143e8880169cfa799fbbbf.apk
         */

        private String __type;
        private String cdn;
        private String filename;
        private String group;
        private String url;

        public PathBean() {
        }

        protected PathBean(Parcel in) {
            this.__type = in.readString();
            this.cdn = in.readString();
            this.filename = in.readString();
            this.group = in.readString();
            this.url = in.readString();
        }

        public String get__type() {
            return __type;
        }

        public void set__type(String __type) {
            this.__type = __type;
        }

        public String getCdn() {
            return cdn;
        }

        public void setCdn(String cdn) {
            this.cdn = cdn;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.__type);
            dest.writeString(this.cdn);
            dest.writeString(this.filename);
            dest.writeString(this.group);
            dest.writeString(this.url);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.channel);
        dest.writeString(this.createdAt);
        dest.writeString(this.objectId);
        dest.writeParcelable(this.path, flags);
        dest.writeString(this.platform);
        dest.writeValue(this.target_size);
        dest.writeString(this.update_log);
        dest.writeString(this.updatedAt);
        dest.writeString(this.version);
        dest.writeString(this.android_url);
        dest.writeInt(this.version_i);
        dest.writeByte(this.isforce ? (byte) 1 : (byte) 0);
    }

    protected BmobAppBean(Parcel in) {
        this.channel = in.readString();
        this.createdAt = in.readString();
        this.objectId = in.readString();
        this.path = in.readParcelable(PathBean.class.getClassLoader());
        this.platform = in.readString();
        this.target_size = (Long) in.readValue(Long.class.getClassLoader());
        this.update_log = in.readString();
        this.updatedAt = in.readString();
        this.version = in.readString();
        this.android_url = in.readString();
        this.version_i = in.readInt();
        this.isforce = in.readByte() != 0;
    }

    public static final Creator<BmobAppBean> CREATOR = new Creator<BmobAppBean>() {
        @Override
        public BmobAppBean createFromParcel(Parcel source) {
            return new BmobAppBean(source);
        }

        @Override
        public BmobAppBean[] newArray(int size) {
            return new BmobAppBean[size];
        }
    };
}
