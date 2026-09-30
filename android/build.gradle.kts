group = "moe.yashi.easylink_flutter"
version = "1.0"

buildscript {
    repositories {
        google()
        mavenCentral()
        // easylinkv3 原本只发布在 jcenter，jcenter 下线后改用阿里云镜像获取。
        maven { url = uri("https://maven.aliyun.com/repository/jcenter") }
    }

    dependencies {
        classpath("com.android.tools.build:gradle:9.1.0")
    }
}

// 依赖解析使用「声明依赖的 configuration 所属项目」的仓库集合，
// 因此这里用 rootProject.allprojects，让 easylinkv3 的仓库对宿主 App 项目同样生效。
rootProject.allprojects {
    repositories {
        google()
        mavenCentral()
        // easylinkv3 原本只发布在 jcenter，jcenter 下线后改用阿里云镜像获取。
        maven { url = uri("https://maven.aliyun.com/repository/jcenter") }
    }
}

plugins {
    id("com.android.library")
}

android {
    namespace = "moe.yashi.easylink_flutter"

    compileSdk = 36

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    defaultConfig {
        minSdk = 24
    }
}

dependencies {
    implementation("io.fogcloud.sdk:easylinkv3:0.1.5") {
        // 该 aar 的 pom 声明了已废弃的 com.android.support:appcompat-v7，
        // 与 AndroidX 会产生重复类冲突；实测其字节码未引用 support 库，故排除。
        exclude(group = "com.android.support")
    }
}
