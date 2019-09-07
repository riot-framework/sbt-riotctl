import ReleaseTransformations._

name := "sbt-riotctl"
version := "0.1"
organization := "org.riot-framework"

sbtPlugin := true

// resolvers += "Local Maven Repository" at Path.userHome.asFile.toURI.toURL + ".m2/repository"

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.4.0")

libraryDependencies ++= Seq(
  "org.riot-framework" % "riotctl" % "0.1"
)

scalacOptions in Compile ++= Seq("-encoding", "UTF-8", "-deprecation", "-feature")
javacOptions in Compile ++= Seq("-encoding", "UTF-8")

//
// For publication to the Sonatype Repo:
//
lazy val releasePublishSettings = Seq(
  releaseCrossBuild := true,
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  releaseProcess := Seq[ReleaseStep](
    /** This is the list of tasks to tell the `sbt-release` plugin to run.
    */
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    publishArtifacts,
    setNextVersion,
    commitNextVersion,
    ReleaseStep(action = Command.process("sonatypeReleaseAll", _)),
    pushChanges
  ),
  homepage := Some(url("https://github.com/riot-framework/sbt-riotctl")),
  licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0")),
  publishMavenStyle := true,
  publishConfiguration := publishConfiguration.value.withOverwrite(true),
  publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true),
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  useGpg := true,
  pgpSecretRing := file("~/.gnupg/pubring.kbx"),
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  scmInfo := Some(ScmInfo(url("https://github.com/riot-framework/sbt-riotctl"),
                            "scm:git:git@github.com:riot-framework/sbt-riotctl.git")),
  developers := List(Developer("fauberso",
                             "Frederic Auberson",
                             "frederic@auberson.net",
                             url("https://github.com/fauberso")))
)