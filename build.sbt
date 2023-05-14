val scala3Version = "3.2.2"

lazy val root = project
  .in(file("."))
  .settings(
    name := "hackattic-scala",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies += "com.softwaremill.sttp.client4" %% "core" % "4.0.0-M1",
    libraryDependencies += "com.lihaoyi" %% "upickle" % "3.0.0",
    libraryDependencies += "com.google.zxing" % "core" % "3.5.1",
    libraryDependencies += "com.google.zxing" % "javase" % "3.5.1",

    libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test
  )
