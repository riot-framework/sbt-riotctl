package riot.riotctl.sbt

import sbt._
import sbt.Keys.{ sLog, target }
import riot.riotctl.Target
import riot.riotctl.RiotCtlTool
import scala.collection.Seq
import scala.collection.JavaConverters
import com.typesafe.sbt._
import com.typesafe.sbt.packager.Keys._
import sbt.internal.LogManager

object RiotCtl extends AutoPlugin {
  override val trigger: PluginTrigger = allRequirements
  override val requires: Plugins = plugins.JvmPlugin && packager.universal.UniversalPlugin && packager.archetypes.systemloader.SystemdPlugin

  object Keys {
    lazy val riotPrereqs = settingKey[String]("Prerequisite apt-get packages, space-separated. Defaults to Java 8 and WiringPi.")
    lazy val riotTargets = taskKey[Seq[riotTarget]]("Address and access credentials of the target device (e.g. 'raspberrypi', 'pi', 'raspberry')")
    
    lazy val install = taskKey[Unit]("Installs an application to a Linux SBC.")
  }

  case class riotTarget(valHostname: String, valUsername: String, valPassword: String) extends Target(Target.DiscoveryMethod.hostname, valHostname, valUsername, valPassword)

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
    val pkg = new File(pkgName)
    val dir = stage.value
    
    val u = new RiotCtlTool(pkgName, JavaConverters.seqAsJavaList(riotTargets.value), new sbtLogger(log))

    u.ensurePackages(riotPrereqs.value)
    
    log.info(s"Deploying $pkgName from '$dir'")
    u.install(pkg)
    
    u.close();
    
    pkg
  }
  
}
