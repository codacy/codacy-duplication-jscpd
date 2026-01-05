scalaVersion := "2.13.16"

name := "codacy-duplication-jscpd"

libraryDependencies ++= Seq(
  "com.codacy" %% "codacy-duplication-scala-seed" % "2.1.4",
  "org.scalameta" %% "svm-subs" % "20.2.0"
)
enablePlugins(GraalVMNativeImagePlugin)
graalVMNativeImageGraalVersion := Some("21.0.0")
graalVMNativeImageOptions ++= Seq(
  "-O1",
  "-H:+ReportExceptionStackTraces",
  "--no-fallback",
  "--no-server",
  "--static"
)
