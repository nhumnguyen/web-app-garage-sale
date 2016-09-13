name := """project"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean, SbtWeb)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  "org.avaje.ebeanorm" % "avaje-ebeanorm" % "4.6.2",
  javaWs,
  evolutions
)
