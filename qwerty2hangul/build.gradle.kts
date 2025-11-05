import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("java-library")
    kotlin("jvm")
    alias(libs.plugins.vanniktech.maven.publish)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    testImplementation(libs.junit)
}

mavenPublishing {
    coordinates(
        groupId = "io.github.easyhooon",
        artifactId = "qwerty2hangul",
        version = "1.0.0"
    )

    pom {
        name.set("Qwerty2Hangul")
        description.set("Convert QWERTY-typed Korean to actual Hangul characters")
        inceptionYear.set("2025")
        url.set("https://github.com/easyhooon/qwerty2hangul")

        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
            }
        }

        developers {
            developer {
                id.set("easyhooon")
                name.set("Lee jihun")
                email.set("mraz3068@gmail.com")
            }
        }

        scm {
            url.set("https://github.com/easyhooon/qwerty2hangul")
            connection.set("scm:git:git://github.com/easyhooon/qwerty2hangul.git")
            developerConnection.set("scm:git:ssh://git@github.com/easyhooon/qwerty2hangul.git")
        }
    }

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
}