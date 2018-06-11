name := "test-akka-http"

version := "0.1"

scalaVersion := "2.11.8"

libraryDependencies ++= {
  val akkaV = "2.4.8"
  val scalaTestV = "2.2.6"
  Seq(
    "com.typesafe"      %  "config"                             % "1.3.0",
    "com.typesafe.akka" %% "akka-http-core"                     % akkaV,
    "com.typesafe.akka" %% "akka-stream"                        % akkaV,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental"  % akkaV,
    "com.typesafe.akka" %% "akka-http-testkit-experimental"     % "2.4.2-RC3",
    "org.scalatest"     %% "scalatest"                          % scalaTestV % "test"
  )
}