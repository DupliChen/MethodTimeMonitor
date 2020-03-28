package com.hyperion.methodmonitor.business;

import android.os.Looper;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 这个类用于插桩使用，千万不要乱动。不要改名、挪位置。
 * author: chenchen38
 * created at 2020/3/28
 */
public final class MethodTimeManager {

    public static final String RETURN_TEXT = "..";
    private static volatile MethodTimeManager sInstance;
    private boolean mIsEnable;
    private boolean mIsOpen = true;
    // 缓存所有收集到的方法调用信息
    private List<List<MethodTimeInfo>> mCache = new ArrayList<>();
    // 缓存start时的数据，在stop时移除的同时要设置结束时间
    private Map<String, MethodTimeInfo> mReadyStopCache = new ConcurrentHashMap<>();

    private MethodTimeManager() {
    }

    public static MethodTimeManager getInstance() {
        if (sInstance == null) {
            synchronized (MethodTimeManager.class) {
                if (sInstance == null) {
                    sInstance = new MethodTimeManager();
                }
            }
        }
        return sInstance;
    }

    public void setEnable(boolean mIsEnable) {
        this.mIsEnable = mIsEnable;
    }

    public boolean isEnable() {
        return mIsEnable;
    }

    public void setOpen(boolean mIsOpen) {
        this.mIsOpen = mIsOpen;
    }

    /**
     * 用于给插桩插件调用，勿动
     */
    public synchronized void onMethodStart(String className, String methodName, String methodDescriptor) {
        if (!mIsEnable || !mIsOpen) {
            return;
        }
        // 只监听主线程
        if (Looper.myLooper() != Looper.getMainLooper()) {
            return;
        }

        if (TextUtils.isEmpty(className) || TextUtils.isEmpty(methodName) || TextUtils.isEmpty(methodDescriptor)) {
            return;
        }
        String[] arrays = className.split("/");
        if (arrays.length == 0) {
            return;
        }

        String parentPackagePartName = null;
        for (int i = 0; i < arrays.length; i++) {
            List<MethodTimeInfo> list = null;
            if (i < mCache.size()) {
                list = mCache.get(i);
            } else {
                // 新建
                list = new ArrayList<>();
                mCache.add(list);
            }
            if (list == null) {
                list = new ArrayList<>();
            }
            mCache.set(i, list);

            MethodTimeInfo info = null;
            if (i == arrays.length - 1) {
                // 最后一个节点，一定是类节点
                info = new MethodTimeInfo();
                info.mParentPackagePartName = parentPackagePartName;
                info.mClassName = arrays[i];
                info.mMethodName = methodName;
                info.mMethodDesc = methodDescriptor;
                info.mStartTime = System.currentTimeMillis();
                mReadyStopCache.put(generateKey(className, methodName, methodDescriptor), info);
            } else {
                // 其他都是包节点
                info = new MethodTimeInfo();
                info.mParentPackagePartName = parentPackagePartName;
                info.mPackagePartName = arrays[i];
                parentPackagePartName = arrays[i];
            }
            if (!list.contains(info)) {
                list.add(info);
            }
        }
    }

    /**
     * 用于给插桩插件调用，勿动
     */
    public void onMethodEnd(String className, String methodName, String methodDescriptor) {
        if (!mIsEnable || !mIsOpen) {
            return;
        }
        // 只监听主线程
        if (Looper.myLooper() != Looper.getMainLooper()) {
            return;
        }

        if (TextUtils.isEmpty(className) || TextUtils.isEmpty(methodName) || TextUtils.isEmpty(methodDescriptor)) {
            return;
        }

        MethodTimeInfo methodTimeInfo = mReadyStopCache.remove(generateKey(className, methodName, methodDescriptor));
        // 如果是包节点，则什么都不做
        if (!TextUtils.isEmpty(methodTimeInfo.mPackagePartName)) {
            return;
        }
        methodTimeInfo.mStopTime = System.currentTimeMillis();
    }

    /**
     * 根据上一级包名和设置的耗时时间，获取函数列表
     */
    public synchronized List<MethodTimeInfo> getMethodTimeInfo(int level, String parentPackagePartName, long execTime) {
        if (level < 0 || level >= mCache.size()) {
            return null;
        }
        List<MethodTimeInfo> result = new ArrayList<>();
        if (level != 0) {
            MethodTimeInfo returnInfo = new MethodTimeInfo();
            returnInfo.mPackagePartName = RETURN_TEXT;
            result.add(returnInfo);
        }
        for (MethodTimeInfo info : mCache.get(level)) {
            if (getExecTime(info, level) >= execTime) {
                if (TextUtils.isEmpty(parentPackagePartName)) {
                    result.add(info);
                } else if (TextUtils.equals(info.mParentPackagePartName, parentPackagePartName)) {
                    result.add(info);
                }
            }
        }
        return result;
    }

    /**
     * 递归获取执行时间
     */
    private long getExecTime(MethodTimeInfo info, int level) {
        if (info == null) {
            return 0;
        }
        if (TextUtils.isEmpty(info.mPackagePartName)) {
            return info.mStopTime - info.mStartTime;
        } else {
            List<MethodTimeInfo> methodTimeInfos = mCache.get(level + 1);
            long maxExecTime = 0;
            for (MethodTimeInfo methodTimeInfo : methodTimeInfos) {
                if (TextUtils.equals(methodTimeInfo.mParentPackagePartName, info.mPackagePartName)) {
                    long execTime = getExecTime(methodTimeInfo, level + 1);
                    if (execTime > maxExecTime) {
                        maxExecTime = execTime;
                    }
                }
            }
            return maxExecTime;
        }
    }

    private String generateKey(String className, String methodName, String methodDescriptor) {
        return className + methodName + methodDescriptor;
    }
}
