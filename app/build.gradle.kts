import org.gradle.testing.jacoco.tasks.JacocoReport

import java.util.Properties

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties().apply {
    if (keystorePropertiesFile.exists()) {
        load(keystorePropertiesFile.inputStream())
    }
}

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
    id("com.google.dagger.hilt.android")
    id("jacoco")
    id("io.github.takahirom.roborazzi")
}

android {
    namespace ="com.kireaji.minimallauncherapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.kireaji.minimallauncherapp"
        minSdk = 26
        targetSdk = 36
        versionCode = 140
        versionName = "1.4.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            storeFile = keystoreProperties["storeFile"]?.let { file(it as String) }
            storePassword = keystoreProperties["storePassword"] as String?
            keyAlias = keystoreProperties["keyAlias"] as String?
            keyPassword = keystoreProperties["keyPassword"] as String?
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled =  false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    testOptions {
        unitTests {
            // VRT 必須。Robolectric に values/ と values-night/ を読ませる。
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {

    implementation ("org.jetbrains.kotlin:kotlin-stdlib:1.9.24")
    implementation ("androidx.core:core-ktx:1.13.1")
    implementation (platform("org.jetbrains.kotlin:kotlin-bom:1.9.24"))
    implementation ("androidx.activity:activity-compose:1.9.0")
    // Compose UI 1.6.8 / Material3 1.2.1。Kotlin 1.9.24 + compose compiler 1.5.14 で使える上限。
    // これより古いと VRT (Roborazzi/Robolectric) の描画が動かない。
    implementation (platform("androidx.compose:compose-bom:2024.06.00"))
    implementation ("androidx.compose.ui:ui")
    implementation ("androidx.compose.ui:ui-graphics")
    implementation ("androidx.compose.ui:ui-tooling-preview")
    implementation ("androidx.compose.material3:material3")
    debugImplementation ("androidx.compose.ui:ui-tooling")
    debugImplementation ("androidx.compose.ui:ui-test-manifest")
    implementation ("com.google.accompanist:accompanist-drawablepainter:0.28.0")
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("androidx.fragment:fragment-ktx:1.5.7")
    implementation ("com.google.android.material:material:1.6.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.2")
    implementation ("androidx.legacy:legacy-support-v4:1.0.0")
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    // Hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    ksp("com.google.dagger:hilt-compiler:2.51.1")

    implementation (platform("com.google.firebase:firebase-bom:31.2.2"))
    implementation ("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-crashlytics")
    implementation ("com.google.firebase:firebase-perf")

    // JUnit
    testImplementation("junit:junit:4.13.2")
    // Mockito
    testImplementation("org.mockito:mockito-inline:3.12.4")
    // VRT (Visual Regression Testing) — docs/vrt.md
    // Roborazzi は 1.47.0 に固定。1.48.0 以降は kotlin-stdlib 2.0.21 でビルドされており
    // Kotlin 1.9.24 のコンパイラが metadata version を弾く。
    testImplementation("org.robolectric:robolectric:4.16.1")
    testImplementation("io.github.takahirom.roborazzi:roborazzi:1.47.0")
    testImplementation("io.github.takahirom.roborazzi:roborazzi-compose:1.47.0")
    androidTestImplementation ("androidx.test.ext:junit:1.1.1")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.2.0")
}


jacoco {
    toolVersion = "0.8.8"
    reportsDirectory.set(layout.buildDirectory.dir("customJacocoReportDir"))
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        csv.required.set(true)
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }

    val excludes = listOf(
            "**/R.class",
            "**/R$*.class",
            "**/BuildConfig.*",
            "**/Manifest*.*",
            "**/*Test*.*",
            "android/**/*.*"
    )
    val debugTree = fileTree(
            mapOf(
                    "dir" to "$buildDir/tmp/kotlin-classes/debug",
                    "include" to "**/*UseCase.*",
                    "excludes" to excludes
            )
    )
    val mainSrc = "${project.projectDir}/src/main/java"

    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(fileTree(mapOf("dir" to "$buildDir", "includes" to listOf("jacoco/testDebugUnitTest.exec"))))
}

// ─────────────────────────── VRT (Visual Regression Testing) ───────────────────────────
// 使い方は docs/vrt.md を参照。
//   ./gradlew :app:vrtRecord  -> vrt/expect/ を再生成（意図した UI 変更時。コミットする）
//   ./gradlew :app:vrtVerify  -> vrt/actual/ と vrt/diff/ を生成し、差分があれば失敗する

val vrtRoot: Directory = rootProject.layout.projectDirectory.dir("vrt")
val vrtExpectDir: File = vrtRoot.dir("expect").asFile
val vrtActualDir: File = vrtRoot.dir("actual").asFile
val vrtDiffDir: File = vrtRoot.dir("diff").asFile

tasks.withType<Test>().configureEach {
    // Vrt.kt が期待画像の絶対パスを組み立てるのに使う。
    systemProperty("vrt.root", vrtRoot.asFile.absolutePath)
    systemProperty("java.awt.headless", "true")
    // 1080x2424 の ARGB_8888 が約 10.5MB/枚。比較用キャンバスはその 3 倍近くになる。
    maxHeapSize = "2g"
    // 期待画像を差し替えたら比較をやり直させる。
    // inputs.dir(..).optional() はディレクトリ自体の不在を許容しない（初回 record 前に落ちる）ので
    // FileTree で受ける。ディレクトリが無ければ単に空になる。
    inputs.files(fileTree(vrtExpectDir))
        .withPropertyName("vrtExpect")
        .withPathSensitivity(PathSensitivity.RELATIVE)
}

tasks.register("vrtRecord") {
    group = "verification"
    description = "期待画像 vrt/expect/ を再生成する。結果をレビューしてコミットすること。"
    dependsOn("recordRoborazziDebug")
}

tasks.register("vrtCollect") {
    group = "verification"
    description = "VRT 比較を実行し vrt/actual/ と vrt/diff/ を生成する（差分があっても失敗しない）。"
    dependsOn("compareRoborazziDebug")

    // Roborazzi 1.47.0 のレイアウト。1.48.0 以降は .../roborazzi/<variant>/results/ になるため
    // バージョンを上げる際はここも直すこと。
    val resultDir = layout.buildDirectory.dir("test-results/roborazzi/results")
    val expectRoot = vrtExpectDir
    val actualRoot = vrtActualDir
    val diffRoot = vrtDiffDir

    outputs.upToDateWhen { false }

    doLast {
        actualRoot.deleteRecursively()
        actualRoot.mkdirs()
        diffRoot.deleteRecursively()
        diffRoot.mkdirs()

        val jsonFiles = resultDir.get().asFile
            .listFiles { f: File -> f.isFile && f.name.endsWith(".json") }
            .orEmpty()
        check(jsonFiles.isNotEmpty()) {
            "Roborazzi の結果が ${resultDir.get().asFile} にありません。VRT テストが実際に実行されたか確認してください。"
        }

        val changed = mutableListOf<String>()
        val added = mutableListOf<String>()
        val seen = mutableSetOf<String>()

        jsonFiles.forEach { jsonFile ->
            @Suppress("UNCHECKED_CAST")
            val result = groovy.json.JsonSlurper().parse(jsonFile) as Map<String, Any?>
            val golden = File(result["golden_file_path"] as String)
            // 例: "pixel10/app-list-ideal.png"
            val rel = golden.relativeTo(expectRoot).path
            seen += rel

            fun place(src: Any?, destRoot: File) {
                val source = File(src as? String ?: return)
                if (!source.exists()) return
                val dest = File(destRoot, rel).apply { parentFile.mkdirs() }
                source.copyTo(dest, overwrite = true)
            }

            when (val type = result["type"] as String) {
                // 差分ゼロのとき Roborazzi は actual を書かない（actual_file_path は null）。
                // デフォルトの ThresholdValidator(0F) は完全一致判定なので、期待画像をそのまま
                // actual として置くのは正しい。
                "unchanged" -> golden.copyTo(
                    File(actualRoot, rel).apply { parentFile.mkdirs() },
                    overwrite = true
                )
                "changed" -> {
                    place(result["actual_file_path"], actualRoot)
                    place(result["compare_file_path"], diffRoot)
                    changed += rel
                }
                "added" -> {
                    place(result["actual_file_path"], actualRoot)
                    place(result["compare_file_path"], diffRoot)
                    added += rel
                }
                "recorded" -> Unit // record 実行時。期待画像を書いた直後なので何もしない。
                else -> logger.warn("VRT: 未知の結果 type '$type' ($rel)")
            }
        }

        // 期待画像はあるが今回のキャプチャに現れなかったもの（画面や状態を消したのに
        // 期待画像を消し忘れているケース）。
        val stale = expectRoot.walkTopDown()
            .filter { it.isFile && it.extension == "png" }
            .map { it.relativeTo(expectRoot).path }
            .filterNot { it in seen }
            .toList()

        logger.lifecycle(buildString {
            appendLine("VRT: ${seen.size} 件を比較しました。")
            if (added.isNotEmpty()) appendLine("  期待画像なし（新規）: ${added.joinToString()}")
            if (changed.isNotEmpty()) appendLine("  差分あり:             ${changed.joinToString()}")
            if (stale.isNotEmpty()) appendLine("  未使用の期待画像:     ${stale.joinToString()}")
        })
    }
}

tasks.register("vrtVerify") {
    group = "verification"
    description = "vrt/actual/ と vrt/diff/ を生成し、vrt/expect/ と差分があれば失敗する。"
    dependsOn("vrtCollect")

    val diffRoot = vrtDiffDir

    doLast {
        val diffs = diffRoot.walkTopDown()
            .filter { it.isFile && it.extension == "png" }
            .toList()
        if (diffs.isNotEmpty()) {
            throw GradleException(
                "VRT: ${diffs.size} 件の差分があります:\n" +
                    diffs.joinToString("\n") { "  - " + it.relativeTo(diffRoot).path } +
                    "\n\nvrt/diff/ の画像を確認してください（左から 期待 | 現状 | 差分）。\n" +
                    "意図した UI 変更なら ./gradlew :app:vrtRecord で期待画像を更新し、vrt/expect/ をコミットします。\n" +
                    "詳細は docs/vrt.md。"
            )
        }
    }
}
