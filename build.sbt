scalaVersion := "2.13.3"

name := "codacy-duplication-jscpd"

libraryDependencies ++= Seq("com.codacy" %% "codacy-duplication-scala-seed" % "2.0.1")
enablePlugins(GraalVMNativeImagePlugin)
val graalVersion = "20.2.0-java11"
graalVMNativeImageGraalVersion := Some(graalVersion)
graalVMNativeImageOptions ++= Seq(
  "-O1",
  "-H:+ReportExceptionStackTraces",
  "--no-fallback",
  "--no-server",
  "--initialize-at-build-time",
  "--report-unsupported-elements-at-runtime",
  "--static")
