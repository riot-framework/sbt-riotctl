package riot.riotctl.sbt

import sbt._
import sbt.Keys.{ sLog, target }
import sbt.plugins._
import riot.riotctl.Target
import riot.riotctl.RiotCtlTool
import scala.collection.Seq
import scala.collection.JavaConverters
import com.typesafe.sbt._
import com.typesafe.sbt.packager.Keys._
import sbt.internal.LogManager
import com.typesafe.sbt.SbtNativePackager.{Debian, Linux, Universal}
import com.typesafe.sbt.packager.linux.LinuxKeys
import com.typesafe.sbt.packager.debian.DebianPlugin
import com.typesafe.sbt.packager.archetypes.JavaServerAppPackaging
import com.typesafe.sbt.packager.archetypes.systemloader.SystemdPlugin
import com.typesafe.sbt.packager.debian.DebianKeys
object RiotCtl extends AutoPlugin {
  override val trigger: PluginTrigger = allRequirements
  override val requires: Plugins = JvmPlugin && JavaServerAppPackaging && DebianPlugin && SystemdPlugin

  object Keys {
    lazy val riotPrereqs = settingKey[String]("Prerequisite apt-get packages, space-separated. Defaults to Java 8 and WiringPi.")
    lazy val riotTargets = taskKey[Seq[riotTarget]]("Address and access credentials of the target device (e.g. 'raspberrypi', 'pi', 'raspberry')")

    lazy val install = taskKey[Unit]("Installs an application to a Linux SBC.")
  }

  case class riotTarget(valHostname: String, valUsername: String, valPassword: String) extends Target(Target.DiscoveryMethod.mdns_then_host, valHostname, valUsername, valPassword)

  class sbtLogger(log: Logger) extends riot.riotctl.Logger {
    override def debug(s: String): Unit = {
      log.debug(s)
    }
    override def info(s: String): Unit = {
      log.info(s)
    }
    override def error(s: String): Unit = {
      log.error(s)
    }
  }

  val autoImport = Keys
  import autoImport._

  override lazy val projectSettings: Seq[Setting[_]] = Seq(
    riotTargets := Seq(
      riotTarget("raspberrypi.local", "pi", "raspberry")),
    riotPrereqs := "oracle-java8-jdk wiringpi",
    install := installTask.value)

  private def installTask = Def.task {
    val log = sLog.value
    val pkgName = packageName.value
    val stageDir = stage.value
    val systemdConf = (linuxMakeStartScript in Debian).value

    val u = new RiotCtlTool(pkgName, JavaConverters.seqAsJavaList(riotTargets.value), new sbtLogger(log))

    u.ensurePackages(riotPrereqs.value)

    systemdConf match {
      case Some(confFile) =>
        u.install(stageDir, confFile)
      case None =>
        log.info("No configuration file found, application was only copied but not installed")
    }

    u.close();

    stageDir
  }

}
