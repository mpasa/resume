val root = project.in(file(".")).settings(
  name := "Resume",
  scalaVersion := "2.12.7",
  scalacOptions := Seq(
    "-deprecation",
    "-Xfatal-warnings"
  ),
  libraryDependencies ++= Seq(
    "com.lihaoyi" %% "scalatags" % "0.6.7",
    "com.lihaoyi" %% "ammonite-ops" % "1.3.2"
  )
)