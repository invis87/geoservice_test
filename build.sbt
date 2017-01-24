name := "Geoservice_test"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= {
  val akkaV = "2.4.16"
  val sprayV = "1.3.3"
  Seq(
    "io.spray"                   %%  "spray-can"       % sprayV,
    "io.spray"                   %%  "spray-routing"   % sprayV,
    "io.spray"                   %%  "spray-json"      % sprayV,
    "io.spray"                   %%  "spray-testkit"   % sprayV  % "test",

    "com.typesafe.akka"          %%  "akka-actor"      % akkaV,
    "com.typesafe"               %   "config"          % "1.3.1",

    "ch.qos.logback"             %   "logback-classic" % "1.1.7",
    "com.typesafe.scala-logging" %%  "scala-logging"   % "3.5.0",
    "com.typesafe.akka"          %%  "akka-slf4j"      % "2.4.16", //todo: remove maybe

    "org.specs2"                 %%  "specs2-core"     % "3.8.6"  % "test"
  )
}

fork in run := true

cancelable in Global := true