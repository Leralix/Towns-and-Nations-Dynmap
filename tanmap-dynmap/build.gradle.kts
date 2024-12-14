plugins {
    id("java")
}
dependencies {
    implementation(project(mapOf("path" to ":")))
}



tasks.test {
    useJUnitPlatform()
}