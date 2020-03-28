# MethodTimeMonitor 功能介绍

基于ASM进行Class文件插桩，对配置的白名单包名下所有的Class的所有方法进行插桩。

对所有的方法开始前插入埋点代码，方法结束时插入埋点代码。在埋点代码中判断是否是主线程。如果是主线程，则搜集类和方法的信息，以及这个方法的执行时间。

提供一个内置的Activity，用于展现插桩得到的数据。用户输入期望过滤的耗时时长，比如期望过滤主线程执行200ms以上的方法。然后以包名折叠的方式展示出来。

# 集成方法

## 1. 添加仓库

```
repositories {
    maven { url 'https://www.jitpack.io' }
}
```

## 2. 添加插件

插件依赖的gradle是3.1.0版本的，如果工程依赖版本过高或过低，可能会编译不过

```
dependencies {
    classpath 'com.github.HyperionChen.MethodTimeMonitor:plugin:1.0.2'
}
```

## 3. 实现依赖

在 app module 的 build.gradle 中添加依赖

```
implementation 'com.github.HyperionChen.MethodTimeMonitor:business:1.0.2'
```

## 4. 应用插件

在 app module 的 build.gradle 顶部添加

```
apply plugin: 'com.hyperion.methodmonitor.plugin'
```

## 5. 配置插件开关，以及包名白名单和黑名单

在根目录的 gradle.properties 添加如下配置

```
# 是否开启插桩
HC_OPEN_MONITOR=true
# 包名前缀白名单，只有在白名单之内的包名的Class文件才会被插桩，报名之间用英文逗号,隔开
HC_PACKAGE_PREFIX_WHITELIST=com/iflytek/easytrans
# 包名前缀黑名单，当满足白名单的包名的子包不希望被插桩时，可以设置黑名单的方式跳过插桩
HC_PACKAGE_PREFIX_BLACKLIST=com/iflytek/easytrans/testmodule
```

## 6. 开启或关闭函数监听

在 Application 的 onCreate 方法开启监听

```
MethodTimeManager.getInstance().setEnable(true);
```

在 Application 的 onTerminate 方法关闭监听

```
MethodTimeManager.getInstance().setEnable(false);
```

## 7. 调起数据列表页面

可通过 adb 命令调起内置的 Activity 进行数据查看

```
adb shell am start -n 包名/com.hyperion.methodmonitor.business.MethodTimeActivity
```

# 注意事项

## 1. 跟其他插桩插件有冲突

本插件采用 ASM 插桩技术实现，因此会和其他插桩插件冲突。因此在配置后发现无法编译，可以尝试先注释掉其他的插桩插件。

注意要把 module 的 build.gradle 的 apply... 注释，还要把根目录的 build.gradle 的 classpath... 也注释掉。

目前已知的会跟 ezcoco 和一些性能监控的冲突

## 2. 兼容 JAVA8

如果项目中使用的是 JAVA8，并且使用了 Lamdba 表达式，那么可能会遇到编译不过的情况。这个时候可以尝试在根目录的 gradle.properties 添加如下配置

```
# 是否使用D8编译器替代DX，如果打包出现问题，把下面这行配置改为false
android.enableD8=true
# 是否允许脱糖，设置为false会把所有lamdba表达式转换为内部类而不是静态表达式
android.enableD8.desugaring=false
```

## 3. 巧用白名单和黑名单机制

如果项目中有一些类一直插桩失败，可以利用白名单和黑名单机制过滤掉这些类

# 数据查看效果

![Demo](https://wpsfile.ksyun.com/a9f4459266dae8e56b6bfdf8ba67d02ecf9845f8?response-content-disposition=attachment%3Bfilename%2A%3Dutf-8%27%27MethodTimeMonitorDemo.png&KSSAccessKeyId=AKLTVHhAfrxsSC2bGR_WE8shnA&Expires=1585411665&Signature=CMKLJDNsdIiabCKdpPPhwMa%2BYjY%3D)
