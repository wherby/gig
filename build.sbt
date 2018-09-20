import com.typesafe.sbt.SbtMultiJvm.multiJvmSettings
import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.MultiJvm
import sbt.url
import sbt._
import Keys._
import sbtassembly.AssemblyPlugin.autoImport._
import Dependencies._




//https://github.com/cakesolutions/scala-kafka-client/wiki/Scala-Kafka-Client

lazy val `gig` = project
  .in(file("."))
  .settings(multiJvmSettings: _*)
  .settings(
    name := "gig",
    version := "0.2.1-SNAPSHOT",
    organization := "io.github.wherby",
    scalaVersion := "2.12.2",
    scalacOptions in Compile ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlog-reflective-calls", "-Xlint"),
    javacOptions in Compile ++= Seq("-Xlint:unchecked", "-Xlint:deprecation"),
    javaOptions in run ++= Seq("-Xms128m", "-Xmx1024m", "-Djava.library.path=./target/native"),
    libraryDependencies ++= Seq(
      //Scala https://github.com/cakesolutions/scala-kafka-client#scala-kafka-client
      "net.cakesolutions" %% "scala-kafka-client" % versions.scalakafkaclient,
      "net.cakesolutions" %% "scala-kafka-client-akka" % versions.scalakafkaclient,
      "net.cakesolutions" %% "scala-kafka-client-testkit" % versions.scalakafkaclient ,
      "com.typesafe.akka" %% "akka-actor" % versions.akka,
      "com.typesafe.akka" %% "akka-multi-node-testkit" % versions.akka,
      "com.typesafe.scala-logging" %% "scala-logging" % versions.scalalog,
      "com.typesafe.akka" %% "akka-slf4j" % versions.akka,
      "ch.qos.logback" % "logback-classic" % versions.logback,
      "org.scalatest" %% "scalatest" % "3.0.1" % Test,
      "io.kamon" % "sigar-loader" % "1.6.6-rev002"),
    fork in run := false,   //###If the value is true, Ctrl + C may only kill JVM and not kill Akka. Set to false to kill togother.
    //mainClass in (Compile, run) := Some("hydra.cluster.app.SimpleClusterApp"),
    // mainClass in assembly := Some("hydra.cluster.app.SimpleClusterApp"),//object with,
    // disable parallel tests
    test in assembly := {},
    parallelExecution in Test := false,
    licenses := Seq("GPL-3.0" -> url("https://opensource.org/licenses/GPL-3.0"))

  )
  .configs (MultiJvm)


useGpg := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (version.value.contains("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

licenses := Seq("GPL-3.0" -> url("https://opensource.org/licenses/GPL-3.0"))

homepage := Some(url("https://github.com/wherby/gig"))

scmInfo := Some(
  ScmInfo(
    url("https://github.com/wherby/gig.git"),
    "scm:git@github.com:wherby/gig.git"
  )
)

developers := List(
  Developer(
    id    = "wherby",
    name  = "Tao Zhou",
    email = "187225577@qq.com",
    url   = url("https://github.com/wherby")
  )
)