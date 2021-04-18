val root = project
  .in(file("."))
  .settings(
    name := "Resume",
    organization := "me.mpasa",
    version := "0.1-SNAPSHOT",
    scalaVersion := "2.13.5",
    scalacOptions := Seq(
      "-deprecation",
      "-Xfatal-warnings"
    ),
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "scalatags" % "0.9.4",
      "com.lihaoyi" %% "ammonite-ops" % "2.3.8",
      "org.typelevel" %% "mouse" % "1.0.2"
    )
  )
