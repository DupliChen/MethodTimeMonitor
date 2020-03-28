package com.hyperion.methodmonitor.business;

import android.text.TextUtils;

public class MethodTimeInfo {

    // 上一级包名的部分
    public String mParentPackagePartName;
    // 包名的部分
    public String mPackagePartName;
    // 类名
    public String mClassName;
    public String mMethodName;
    public String mMethodDesc;
    public long mStartTime;
    public long mStopTime;

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof MethodTimeInfo)) {
            return false;
        }
        MethodTimeInfo other = (MethodTimeInfo) obj;
        if (!TextUtils.equals(mParentPackagePartName, other.mParentPackagePartName)) {
            return false;
        }
        if (!TextUtils.isEmpty(mPackagePartName) && !TextUtils.isEmpty(other.mPackagePartName)) {
            return TextUtils.equals(mPackagePartName, other.mPackagePartName);
        }
        return false;
    }
}
