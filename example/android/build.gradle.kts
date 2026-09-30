import com.android.build.api.variant.LibraryAndroidComponentsExtension

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

// Android SDK 仓库把 API 37 的平台包放在 platforms/android-37.0 目录下，而插件
// （如 permission_handler_android）声明 compileSdk = 37 时，AGP 9.1 会去查找
// platforms/android-37 而失败。这里在 DSL 最终化之前为所有库模块显式指定
// minor 版本 0，使其解析到已安装的 android-37.0。
subprojects {
    plugins.withId("com.android.library") {
        extensions.configure<LibraryAndroidComponentsExtension> {
            finalizeDsl { extension ->
                extension.compileSdk = 37
                extension.compileSdkMinor = 0
            }
        }
    }
}

val newBuildDir: Directory =
    rootProject.layout.buildDirectory
        .dir("../../build")
        .get()
rootProject.layout.buildDirectory.value(newBuildDir)

subprojects {
    val newSubprojectBuildDir: Directory = newBuildDir.dir(project.name)
    project.layout.buildDirectory.value(newSubprojectBuildDir)
}
subprojects {
    project.evaluationDependsOn(":app")
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
