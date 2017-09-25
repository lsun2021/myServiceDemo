package com.demo.myservicedemo.demo;

/**
 * Created by ${momoThree} on 2017/9/25.
 * Title:
 */

public interface DownloadLisenter {
    void  onProgress(int progress);
    void  onSuccess();
    void  onFailed();
    void  onPaused();
    void  onCanceled();
}
