package com.gabriel.jg.library.utils;

import android.Manifest;
import android.app.Activity;

import com.gabriel.jg.library.R;


public class CheckPermissionUtil {

    public static final int WRITE_SD_REQ_CODE = 201;
    public static final int CAMERA_REQ_CODE = 202;
    public static final int READ_SD_REQ_CODE = 203;
    private static final int LOCATION_PERMISSION_REQ_CODE = 200;

    public static void checkLocation(Activity activity,
                                     PermissionUtil.ReqPermissionCallback callback) {
        PermissionUtil.checkPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION,
                LOCATION_PERMISSION_REQ_CODE,
                activity.getText(R.string.location_permission_description_message),
                activity.getText(R.string.location_permission_go_settings_message),
                callback);
    }

    public static void checkWriteSd(Activity activity,
                                    PermissionUtil.ReqPermissionCallback callback) {
        PermissionUtil.checkPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                WRITE_SD_REQ_CODE,
                activity.getText(R.string.write_es_permission_description_message),
                activity.getText(R.string.write_es_permission_go_settings_message),
                callback);
    }

    public static void checkCamera(Activity activity,
                                   PermissionUtil.ReqPermissionCallback callback) {
        PermissionUtil.checkPermission(activity,
                Manifest.permission.CAMERA,
                CAMERA_REQ_CODE,
                activity.getText(R.string.message_rationale_camera_permission_get_photo),
                activity.getText(R.string.camera_permission_not_allowed),
                callback);
    }

    public static void checkReadSd(Activity activity,
                                   PermissionUtil.ReqPermissionCallback callback) {
        PermissionUtil.checkPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                READ_SD_REQ_CODE,
                activity.getText(R.string.read_es_permission_description_message),
                activity.getText(R.string.read_es_permission_go_settings_message),
                callback);
    }
}
