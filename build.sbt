name := "scala-messenger-bot"
organization := "com.cpuheater"
version := "0.0.1"

scalaVersion in ThisBuild := "2.11.8"


resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"


libraryDependencies ++= Seq(
 "commons-codec" % "commons-codec" % "1.10",
 "com.typesafe.akka" %% "akka-actor" % "2.4.14",
 "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
 "com.typesafe.akka" % "akka-slf4j_2.11" % "2.4.8",
 "com.typesafe.akka" %% "akka-http" % "10.0.0",
 "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.0"
)
