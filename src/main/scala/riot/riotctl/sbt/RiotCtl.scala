package riot.riotctl.sbt

import sbt._
import sbt.Keys.{ sLog, target }
import riot.riotctl.Target
import riot.riotctl.Util
import scala.collection.Seq
import scala.collection.JavaConverters
import com.typesafe.sbt._

object RiotCtl extends AutoPlugin {
  override val trigger: PluginTrigger = allRequirements
  override val requires: Plugins = plugins.JvmPlugin && packager.universal.UniversalPlugin

  object Keys {
    lazy val riotTargets = taskKey[Seq[riotTarget]]("Host name of the target device (e.g. 'raspberrypi').")
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
    val u = new Util(JavaConverters.seqAsJavaList(riotTargets.value))

    log.info("Deploying to...")

    u
  }
}
