val root = project.in(file(".")).settings(
  name := "Resume",
  organization := "me.mpasa",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.12.7",
  scalacOptions := Seq(
    "-deprecation",
    "-Xfatal-warnings"
  ),
  libraryDependencies ++= Seq(
    "com.lihaoyi"   %% "scalatags"    % "0.6.7",
    "com.lihaoyi"   %% "ammonite-ops" % "1.3.2",
    "org.typelevel" %% "mouse"        % "0.19"
  )
)
