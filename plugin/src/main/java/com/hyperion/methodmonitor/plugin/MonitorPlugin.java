package com.hyperion.methodmonitor.plugin;

import com.android.build.gradle.AppExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

public class MonitorPlugin implements Plugin<Project> {

    private static final String TAG = "MonitorPlugin";

    private static final String KEY_OPEN = "HC_OPEN_MONITOR";
    private static final String KEY_PACKAGE_PREFIX_WHITELIST = "HC_PACKAGE_PREFIX_WHITELIST";
    private static final String KEY_PACKAGE_PREFIX_BLACKLIST = "HC_PACKAGE_PREFIX_BLACKLIST";
    private static final String SPLIT = ",";

    @Override
    public void apply(@NotNull Project project) {
        boolean isOpenMonitor = false;
        if (project.hasProperty(KEY_OPEN)) {
            Object openValue = project.property(KEY_OPEN);
            if (openValue != null) {
                isOpenMonitor = Boolean.parseBoolean(openValue.toString());
            }
        }
        if (!isOpenMonitor) {
            System.out.println(TAG + ":monitor is close");
            return;
        }
        String[] packagePrefixsWhiteList = null;
        if (project.hasProperty(KEY_PACKAGE_PREFIX_WHITELIST)) {
            Object packageValue = project.property(KEY_PACKAGE_PREFIX_WHITELIST);
            if (packageValue != null) {
                packagePrefixsWhiteList = packageValue.toString().split(SPLIT);
            }
        }
        String[] packagePrefixsBlackList = null;
        if (project.hasProperty(KEY_PACKAGE_PREFIX_BLACKLIST)) {
            Object packageValue = project.property(KEY_PACKAGE_PREFIX_BLACKLIST);
            if (packageValue != null) {
                packagePrefixsBlackList = packageValue.toString().split(SPLIT);
            }
        }
        MonitorTransform transform = new MonitorTransform(project, packagePrefixsWhiteList, packagePrefixsBlackList);
        AppExtension app = project.getExtensions().getByType(AppExtension.class);
        app.registerTransform(transform);
    }
}
