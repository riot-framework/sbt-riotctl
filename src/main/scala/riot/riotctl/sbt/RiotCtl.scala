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
import com.typesafe.sbt.SbtNativePackager.{ Debian, Linux, Universal }
import com.typesafe.sbt.packager.linux.LinuxKeys
import com.typesafe.sbt.packager.debian.DebianPlugin
import com.typesafe.sbt.packager.archetypes.JavaServerAppPackaging
import com.typesafe.sbt.packager.archetypes.systemloader.SystemdPlugin
import com.typesafe.sbt.packager.debian.DebianKeys
object RiotCtl extends AutoPlugin {
  override val trigger: PluginTrigger = allRequirements
  override val requires: Plugins = JvmPlugin && JavaServerAppPackaging

  object Keys {
    lazy val riotPrereqs = settingKey[String]("Prerequisite apt-get packages, space-separated. Defaults to Java 8 and WiringPi.")
    lazy val riotDbgPort = settingKey[Int]("Port number to use for debugging.")
    lazy val riotTargets = taskKey[Seq[riotTarget]]("Address and access credentials of the target device (e.g. 'raspberrypi', 'pi', 'raspberry')")

    lazy val riotRequiresI2C = settingKey[Boolean]("Whether to ensure I2C is enabled when deploying.")
    lazy val riotRequiresSPI = settingKey[Boolean]("Whether to ensure SPI is enabled when deploying.")
    lazy val riotRequiresSerial = settingKey[Boolean]("Whether to ensure the serial port is enabled when deploying.")
    lazy val riotRequiresOnewire = settingKey[Boolean]("Whether to ensure 1Wire is enabled when deploying.")

    lazy val riotInstall = taskKey[Unit]("Installs an application as a Systemd service to a Raspberry Pi or similar device.")
    lazy val riotUninstall = taskKey[Unit]("Remove an aplication from Systemd.")
    lazy val riotRun = taskKey[Unit]("Interactively runs an application remotely on a Raspberry Pi or similar device.")
    lazy val riotDebug = taskKey[Unit]("Debugs an application remotely on a Raspberry Pi or similar device, using jdwp remote debugging.")
    lazy val riotStart = taskKey[Unit]("Starts the application on the remote device.")
    lazy val riotStop = taskKey[Unit]("Stops the application on the remote device.")
  }

  case class riotTarget(valHostname: String, valUsername: String, valPassword: String) extends Target(Target.DiscoveryMethod.HOST_THEN_MDNS, valHostname, valUsername, valPassword)

  class sbtLogger(log: Logger) extends riot.riotctl.Logger {
    override def debug(s: String): Unit = {
      log.debug(s)
    }
    override def info(s: String): Unit = {
      log.info(s)
    }
    override def warn(s: String): Unit = {
      log.warn(s)
    }
    override def error(s: String): Unit = {
      log.error(s)
    }
  }

  val autoImport = Keys
  import autoImport._

  override lazy val projectSettings: Seq[Setting[_]] = Seq(
    riotTargets := Seq(
      riotTarget("raspberrypi", "pi", "raspberry")),
    riotRequiresI2C := false,
    riotRequiresSPI := false,
    riotRequiresSerial := false,
    riotRequiresOnewire := false,
    riotPrereqs := "oracle-java8-jdk wiringpi",
    riotDbgPort := 8000,
    riotInstall := installTask.value,
    riotUninstall := uninstallTask.value,
    riotRun := runTask.value,
    riotDebug := debugTask.value,
    riotStart := startTask.value,
    riotStop := stopTask.value)

  private def installTask = Def.task {
    new RiotCtlTool(packageName.value, stage.value, JavaConverters.seqAsJavaList(riotTargets.value), new sbtLogger(sLog.value))
      .ensurePackages(riotPrereqs.value).ensureEnabled(riotRequiresI2C.value, riotRequiresSPI.value, riotRequiresSerial.value, riotRequiresOnewire.value)
      .deploy().install().close();
  }

  private def uninstallTask = Def.task {
    new RiotCtlTool(packageName.value, stage.value, JavaConverters.seqAsJavaList(riotTargets.value), new sbtLogger(sLog.value))
      .uninstall().close();
  }

  private def runTask = Def.task {
    new RiotCtlTool(packageName.value, stage.value, JavaConverters.seqAsJavaList(riotTargets.value), new sbtLogger(sLog.value))
      .ensurePackages(riotPrereqs.value).ensureEnabled(riotRequiresI2C.value, riotRequiresSPI.value, riotRequiresSerial.value, riotRequiresOnewire.value)
      .deploy().run().close();
  }

  private def debugTask = Def.task {
    new RiotCtlTool(packageName.value, stage.value, JavaConverters.seqAsJavaList(riotTargets.value), new sbtLogger(sLog.value))
      .ensurePackages(riotPrereqs.value).ensureEnabled(riotRequiresI2C.value, riotRequiresSPI.value, riotRequiresSerial.value, riotRequiresOnewire.value)
      .deployDbg(riotDbgPort.value).run().close();
  }

  private def startTask = Def.task {
    new RiotCtlTool(packageName.value, stage.value, JavaConverters.seqAsJavaList(riotTargets.value), new sbtLogger(sLog.value))
      .ensurePackages(riotPrereqs.value).ensureEnabled(riotRequiresI2C.value, riotRequiresSPI.value, riotRequiresSerial.value, riotRequiresOnewire.value)
      .start().close();
  }

  private def stopTask = Def.task {
    new RiotCtlTool(packageName.value, stage.value, JavaConverters.seqAsJavaList(riotTargets.value), new sbtLogger(sLog.value))
      .stop().close();
  }

}
