version = "0.0.2"

project.extra["PluginName"] = "SlayWood"
project.extra["PluginDescription"] = "Woodchopper to make progressive"

dependencies {
    implementation("org.jboss.aerogear:aerogear-otp-java:1.0.0")
}

tasks {
    jar {
        manifest {
            attributes(mapOf(
                "Plugin-Version" to project.version,
                "Plugin-Id" to nameToId(project.extra["PluginName"] as String),
                "Plugin-Provider" to project.extra["PluginProvider"],
                "Plugin-Description" to project.extra["PluginDescription"],
                "Plugin-License" to project.extra["PluginLicense"]
            ))
        }
    }
}
