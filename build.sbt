name := "scala-robots"

version := "1.1.5"

organization := "andrei-heidelbacher"

scalaVersion := "2.11.7"

crossPaths := false

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4",
  "org.scala-lang.modules" %% "scala-xml" % "1.0.3",
  "org.ccil.cowan.tagsoup" % "tagsoup" % "1.2.1",
  "org.scalatest" %% "scalatest" % "2.2.3" % "test"
)

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-unchecked",
  "-Xlint",
  "-Xfuture",
  "-Xfatal-warnings",
  "-Ywarn-dead-code"
)
