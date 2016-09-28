import sbt.Keys._

name := "CustomServicesHelper"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  "org.mindrot" % "jbcrypt" % "0.3m",
  "com.typesafe.play" %% "play-mailer" % "3.0.1",
  "org.postgresql" % "postgresql" % "9.4-1200-jdbc41",
  "org.javassist" % "javassist" % "3.18.2-GA",
  "org.apache.httpcomponents" % "httpclient" % "4.5.2",
  "com.google.code.gson" % "gson" % "2.3.1",
  filters
)

libraryDependencies += evolutions

resolvers ++= Seq(
  "Apache" at "http://repo1.maven.org/maven2/",
  "jBCrypt Repository" at "http://repo1.maven.org/maven2/org/",
  "Sonatype OSS Snasphots" at "http://oss.sonatype.org/content/repositories/snapshots"
)
routesGenerator := InjectedRoutesGenerator

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)
