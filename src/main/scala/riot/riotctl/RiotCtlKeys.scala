package riot.riotctl

import sbt._

trait RiotCtlKeys {
  lazy val riotTargetHostname = settingKey[String]("Host name of the target device (e.g. 'raspberrypi').")
  lazy val riotLoginUsername = settingKey[String]("The user name used to ssh into the target device (e.g. 'pi').")
  lazy val riotLoginPassword = settingKey[String]("The password used to authenticate to the target device (e.g. 'raspbery').")

  lazy val install = taskKey[Unit]("Installs an application to a Linux SBC.")
}