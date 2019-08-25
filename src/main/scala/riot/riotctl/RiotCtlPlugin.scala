package riot.riotctl

import sbt.Keys.{ sLog, target }
import sbt._
import sbt.io.{ IO, Path }

object ZipPlugin extends AutoPlugin {
  override val trigger: PluginTrigger = noTrigger
  override val requires: Plugins = plugins.JvmPlugin

  object autoImport extends RiotCtlKeys
  import autoImport._
  
  override lazy val projectSettings: Seq[Setting[_]] = Seq(
    riotTargetHostname := "raspberry",
    riotLoginUsername := "pi",
    riotLoginPassword := "raspberry",
    install := installTask.value)
    
  private def installTask = Def.task {
    val log = sLog.value
    
    log.info("Deploying to ...")
    
    riotTargetHostname
  }
}
