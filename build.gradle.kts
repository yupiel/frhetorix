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
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
