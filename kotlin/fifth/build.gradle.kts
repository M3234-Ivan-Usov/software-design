plugins {
    kotlin("jvm") version "1.5.31"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("net.sf.jung:jung-visualization:2.1.1")
    implementation("net.sf.jung:jung-api:2.1.1")
    implementation("net.sf.jung:jung-graph-impl:2.1.1")
    implementation("com.github.vlsi.mxgraph:jgraphx:4.2.2")

}