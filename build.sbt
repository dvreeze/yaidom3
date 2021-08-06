
// Building both for JVM and JavaScript runtimes.

// To convince SBT not to publish any root level artifacts, I had a look at how scala-java-time does it.
// See https://github.com/cquiroz/scala-java-time/blob/master/build.sbt as a "template" for this build file.


// shadow sbt-scalajs' crossProject and CrossType from Scala.js 0.6.x

import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

val scalaVer = "3.0.1"
val crossScalaVer = Seq(scalaVer)

ThisBuild / description  := "Extensible XML query API with multiple DOM-like implementations, 3rd generation"
ThisBuild / organization := "eu.cdevreeze.yaidom3"
ThisBuild / version      := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion       := scalaVer
ThisBuild / crossScalaVersions := crossScalaVer

// With "-source:future", stricter Scala 3 checks are performed than with "-source:3", also in particular w.r.t. pattern matching.
ThisBuild / scalacOptions ++= Seq("-source:future", "-unchecked", "-indent", "-new-syntax", "-Xfatal-warnings")

ThisBuild / Test / publishArtifact := false
ThisBuild / publishMavenStyle := true

ThisBuild / publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value) {
    Some("snapshots" at nexus + "content/repositories/snapshots")
  } else {
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
  }
}

ThisBuild / pomExtra := pomData
ThisBuild / pomIncludeRepository := { _ => false }

ThisBuild / libraryDependencies += "org.scala-lang.modules" %%% "scala-xml" % "2.0.0"

ThisBuild / libraryDependencies += "org.scalatest" %%% "scalatest" % "3.2.9" % Test

ThisBuild / libraryDependencies += "org.scalatestplus" %%% "scalacheck-1-15" % "3.2.9.0" % Test

lazy val root = project.in(file("."))
  .aggregate(yaidom3JVM, yaidom3JS)
  .settings(
    name                 := "yaidom3",
    // Thanks, scala-java-time, for showing us how to prevent any publishing of root level artifacts:
    // No, SBT, we don't want any artifacts for root. No, not even an empty jar.
    publish              := {},
    publishLocal         := {},
    publishArtifact      := false,
    Keys.`package`       := file(""))

lazy val yaidom3 = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full)
  .in(file("."))
  .jvmSettings(
    // By all means, override this version of Saxon if needed, possibly with a Saxon-EE release!

    // TODO Saxon 10.X?
    libraryDependencies += "net.sf.saxon" % "Saxon-HE" % "10.5",

    libraryDependencies += "org.scalacheck" %%% "scalacheck" % "1.15.4" % Test,

    // mimaPreviousArtifacts := Set("eu.cdevreeze.yaidom3" %%% "yaidom3" % "0.1.0")
  )
  .jsSettings(
    // Do we need this jsEnv?
    jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv(),

    libraryDependencies += ("org.scala-js" %%% "scalajs-dom" % "1.1.0").cross(CrossVersion.for3Use2_13), // Hopefully soon not needed anymore

    Test / parallelExecution := false

    // mimaPreviousArtifacts := Set("eu.cdevreeze.yaidom3" %%% "yaidom3" % "0.1.0")
  )

lazy val yaidom3JVM = yaidom3.jvm

lazy val yaidom3JS = yaidom3.js

lazy val pomData =
  <url>https://github.com/dvreeze/yaidom3</url>
  <licenses>
    <license>
      <name>Apache License, Version 2.0</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
      <distribution>repo</distribution>
      <comments>Yaidom3 is licensed under Apache License, Version 2.0</comments>
    </license>
  </licenses>
  <scm>
    <connection>scm:git:git@github.com:dvreeze/yaidom3.git</connection>
    <url>https://github.com/dvreeze/yaidom3.git</url>
    <developerConnection>scm:git:git@github.com:dvreeze/yaidom3.git</developerConnection>
  </scm>
  <developers>
    <developer>
      <id>dvreeze</id>
      <name>Chris de Vreeze</name>
      <email>chris.de.vreeze@caiway.net</email>
    </developer>
  </developers>
