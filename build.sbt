ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-Ymacro-annotations",
)

val http4sVersion = "1.0.0-M39"
val circeVersion = "0.14.1"
val scalaTestVersion = "3.2.12"
val bot4sVersion = "5.6.3"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-effect" % "3.4.8",
  "org.scalaj" %% "scalaj-http" % "2.4.2",
  "net.ruippeixotog" %% "scala-scraper" % "3.0.0",
  "org.asynchttpclient" % "async-http-client" % "2.12.3",
  "com.softwaremill.sttp.client3" %% "async-http-client-backend-cats" % "3.8.13",
  "biz.enef" %% "slogging" % "0.6.2",
)

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-circe",
  "org.http4s" %% "http4s-ember-client",
  "org.http4s" %% "http4s-ember-server",
  "org.http4s" %% "http4s-dsl",
).map(_ % http4sVersion)

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser",
  "io.circe" %% "circe-literal",
  "io.circe" %% "circe-generic-extras",
).map(_ % circeVersion)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % scalaTestVersion % Test
)

libraryDependencies ++= Seq(
  "com.bot4s" %% "telegram-core",
  "com.bot4s" %% "telegram-akka"
).map(_ % bot4sVersion)

addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
//addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

lazy val root = (project in file("."))
  .settings(
    name := "Cavisto"
  )
