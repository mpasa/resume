val root = project
  .in(file("."))
  .settings(
    name := "Resume",
    organization := "me.mpasa",
    version := "0.1-SNAPSHOT",
    scalaVersion := "3.7.4",
    scalacOptions := Seq(
      "-deprecation",
      "-Xfatal-warnings"
    ),
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "scalatags" % "0.13.1",
      "com.lihaoyi" %% "os-lib" % "0.11.6",
      "org.typelevel" %% "mouse" % "1.4.0"
    )
  )
