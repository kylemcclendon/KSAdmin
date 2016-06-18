name := "KSAdmin"

version := "1.0"

scalaVersion := "2.11.8"

resolvers += "Spigot Repo" at "https://hub.spigotmc.org/nexus/content/repositories/snapshots/"
resolvers += "BungeeCord Repo" at "https://oss.sonatype.org/content/repositories/snapshots/"
resolvers += "WorldEdit Repo" at "http://maven.sk89q.com/repo/"

libraryDependencies += "org.spigotmc" % "spigot-api" % "1.10-R0.1-SNAPSHOT" % "provided"
libraryDependencies += "org.bukkit" % "bukkit" % "1.10-R0.1-SNAPSHOT" % "provided"

scalacOptions += "-deprecation"