organization := "riot"

name := "sbt-riotctl"

version := "0.1-SNAPSHOT"
licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

sbtPlugin := true

resolvers += "Local Maven Repository" at Path.userHome.asFile.toURI.toURL + ".m2/repository"

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.4.0")

libraryDependencies ++= Seq(
  "riot" % "riotctl" % "0.1-SNAPSHOT"
)

scalacOptions in Compile ++= Seq("-encoding", "UTF-8", "-deprecation", "-feature")
javacOptions in Compile ++= Seq("-encoding", "UTF-8")