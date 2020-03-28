package com.hyperion.methodmonitor.plugin;

import com.android.build.api.transform.TransformInvocation;

import org.gradle.api.Project;

public interface IMonitorProcessor {

    void process(Project project, TransformInvocation transformInvocation);

}
