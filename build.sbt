import sbt.Keys.{libraryDependencies, publishMavenStyle}
import sbt.url
import xerial.sbt.Sonatype._
import ReleaseTransformations._

lazy val root =
  (project in file("."))
    .settings(
      organization := "org.riot-framework",
      name := "sbt-riotctl",
      version := "0.7.1",
      scalaVersion := "2.12.8",
      sbtPlugin := true
    )
    .settings(releasePublishSettings)
    .settings(libraryDependencies ++= Seq(
	  "org.riot-framework" % "riotctl" % "0.7.1"
	)
)

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.4.0")

// resolvers += "Local Maven Repository" at Path.userHome.asFile.toURI.toURL + ".m2/repository"

scalacOptions in Compile ++= Seq("-encoding", "UTF-8", "-deprecation", "-feature")
javacOptions in Compile ++= Seq("-encoding", "UTF-8")

//
// For publication to the Sonatype Repo:
//
lazy val releasePublishSettings = Seq(
  releaseCrossBuild := true,
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  releaseProcess := Seq[ReleaseStep](
    /** This is the list of tasks to tell the `sbt-release` plugin to run. */
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
  homepage := Some(url("https://riot.community")),
  licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0")),
  sonatypeProjectHosting := Some(GitHubHosting("riot-framework", "sbt-riotctl", "frederic@auberson.net")),
  publishMavenStyle := true,
  publishConfiguration := publishConfiguration.value.withOverwrite(true),
  publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true),
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  useGpg := true,
  pgpSecretRing := file("~/.gnupg/pubring.kbx"),
  publishTo := sonatypePublishTo.value,
  scmInfo := Some(ScmInfo(url("https://github.com/riot-framework/sbt-riotctl"),
                            "scm:git:git@github.com:riot-framework/sbt-riotctl.git")),
  developers := List(Developer("fauberso",
                             "Frederic Auberson",
                             "frederic@auberson.net",
                             url("https://github.com/fauberso")))
)