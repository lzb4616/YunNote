package com.app.linyu.model;

/**
 * Created by zibin on 2014/3/30 0030.
 */
public class ImageInfo {
    public String imageMsg;
    public int imageId;

    public ImageInfo(String imageMsg, int imageId) {
        this.imageId = imageId;
        this.imageMsg = imageMsg;

    }

    public String getImageMsg() {
        return imageMsg;
    }

    public void setImageMsg(String imageMsg) {
        this.imageMsg = imageMsg;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}

