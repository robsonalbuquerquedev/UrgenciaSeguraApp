// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("org.sonarqube") version "4.4.1.3373"
}

tasks.named("sonar").configure {
    doFirst {
        project.extensions.extraProperties["sonar.projectKey"] = project.property("sonar.projectKey")
        project.extensions.extraProperties["sonar.host.url"] = project.property("sonar.host.url")
        project.extensions.extraProperties["sonar.token"] = project.property("sonar.token")
    }
}
