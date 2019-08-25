package riot.riotctl.sbt

import sbt._
import sbt.Keys.{ sLog, target }
import riot.riotctl.Target
import riot.riotctl.Util
import scala.collection.Seq
import scala.collection.JavaConverters
import com.typesafe.sbt._
import com.typesafe.sbt.packager.Keys._

object RiotCtl extends AutoPlugin {
  override val trigger: PluginTrigger = allRequirements
  override val requires: Plugins = plugins.JvmPlugin && packager.universal.UniversalPlugin && packager.archetypes.systemloader.SystemdPlugin

  object Keys {
    lazy val riotTargets = taskKey[Seq[riotTarget]]("Address and access credentials of the target device (e.g. 'raspberrypi', 'pi', 'raspberry')")
    
    lazy val install = taskKey[Unit]("Installs an application to a Linux SBC.")
  }

  case class riotTarget(valHostname: String, valUsername: String, valPassword: String) extends Target(valHostname, valUsername, valPassword)

  val autoImport = Keys
  import autoImport._

  override lazy val projectSettings: Seq[Setting[_]] = Seq(
    riotTargets := Seq(
      riotTarget("raspberrypi", "pi", "raspberry")),
    install := installTask.value)

  private def installTask = Def.task {
    val log = sLog.value

    val archiveFile = stage.value
    
    val u = new Util(archiveFile, JavaConverters.seqAsJavaList(riotTargets.value))
    
    log.info("Deploying '" + archiveFile +"' to...")

    u
  }
}
