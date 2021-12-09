import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.5.6"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id("java")
	kotlin("jvm") version "1.5.31"
	kotlin("plugin.spring") version "1.5.31"
}

group = "com.iscte.ads"
version = "0.0.1-SNAPSHOT"

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("junit:junit:4.13.1")
	implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.2.0")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.5")
	implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.12.5")
    implementation("org.junit.jupiter:junit-jupiter:5.7.0")
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
	compileKotlin {
		kotlinOptions {
			freeCompilerArgs = listOf("-Xjsr305=strict")
			jvmTarget = "11"
		}
	}

	test {
		useJUnitPlatform()
	}

	bootJar {
		archiveFileName.set("app.jar")
	}
}
