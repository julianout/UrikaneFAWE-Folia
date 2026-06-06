tasks.register("build") {
    dependsOn(subprojects.mapNotNull { it.tasks.findByName("build") })
}
