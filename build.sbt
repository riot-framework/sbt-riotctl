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

homepage := Some(url("https://github.com/riot-framework/sbt-riotctl"))

scmInfo := Some(ScmInfo(url("https://github.com/riot-framework/sbt-riotctl"),
                            "scm:git:git@github.com:riot-framework/sbt-riotctl.git"))
                            
developers := List(Developer("fauberso",
                             "Frederic Auberson",
                             "frederic@auberson.net",
                             url("https://github.com/fauberso")))
                             
useGpg := true
pgpSecretRing := file("~/.gnupg/pubring.kbx")
                  
licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))

publishMavenStyle := true
publishConfiguration := publishConfiguration.value.withOverwrite(true)
publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true)

publishTo := Some(
  if (isSnapshot.value)
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
)