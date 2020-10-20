scalaVersion := "2.13.3"

name := "codacy-duplication-jscpd"

val graalVersion = "20.2.0"
libraryDependencies ++= Seq(
  "com.codacy" %% "codacy-duplication-scala-seed" % "2.0.1",
  "org.scalameta" %% "svm-subs" % graalVersion)
enablePlugins(GraalVMNativeImagePlugin)
graalVMNativeImageGraalVersion := Some(s"$graalVersion-java11")
graalVMNativeImageOptions ++= Seq(
  "-O1",
  "-H:+ReportExceptionStackTraces",
  "--no-fallback",
  "--no-server",
  "--initialize-at-build-time",
  "--static")
