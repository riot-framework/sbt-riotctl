organization := "riot"

name := "sbt-riotctl"

version := "0.1-SNAPSHOT"

sbtPlugin := true

resolvers += "Local Maven Repository" at Path.userHome.asFile.toURI.toURL + ".m2/repository"

libraryDependencies ++= Seq(
  "riot" % "riotctl" % "0.1-SNAPSHOT"
)
