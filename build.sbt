val root = project.in(file(".")).settings(
  name := "Resume",
  scalaVersion := "2.12.2",
  scalacOptions := Seq(
    "-deprecation",
    "-Xfatal-warnings"
  ),
  libraryDependencies ++= Seq(
    "com.lihaoyi" %% "scalatags" % "0.6.7",
    "com.lihaoyi" %% "ammonite-ops" % "1.1.2"
  )
)