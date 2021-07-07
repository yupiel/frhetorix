plugins {
    id("application")
    id("org.openjfx.javafxplugin") version "0.0.10"
}

group = "de.yupiel"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("de.yupiel.frhetorix.FrhetorixApplication")
}

javafx {
    version = "16"
    modules("javafx.controls", "javafx.fxml")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.8.7")
    implementation("software.amazon.awssdk:aws-sdk-java:2.16.95")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.2")
}

java {
    modularity.inferModulePath.set(true)
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
