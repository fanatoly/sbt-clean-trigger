package io.github.fanatoly

import sbt._
import sbt.Def._
import sbt.Keys._

object CleanTriggerPlugin extends Plugin{
  val cleanTriggerFile = 
    Def.settingKey[File]("File used to determine whether clean should be run")

  val stampedClean = 
    Def.taskKey[Unit]("Task to clean and update trigger timestamps")

  val triggeredClean =
    Def.taskKey[Unit]("Task that executes the clean task based on whether the trigger is updated")

  private val stampedCleanImpl = Def.task {
    streams.value.log.info(s"Stamped clean about to delete ${cleanFiles.value} but not ${cleanKeepFiles.value}")
    clean.value
    val cacheStamp = Difference.inputs(
      (cacheDirectory in triggeredClean).value,
      FilesInfo.lastModified
    )
    cacheStamp(Set(cleanTriggerFile.value)){ _ => }
  }

  private val dynamicClean = Def.taskDyn[Unit] {
    val cacheStamp = Difference.inputs(
      (cacheDirectory in triggeredClean).value,
      FilesInfo.lastModified
    )
    val triggerFile = cleanTriggerFile.value
    val theProj = thisProjectRef.value
    val shouldClean = cacheStamp(Set(triggerFile)){ _.modified.nonEmpty }
    val filter = ScopeFilter( inAggregates(theProj) )

    if(shouldClean) Def.task{
      streams.value.log.info(s"A change to ${triggerFile} has triggered a clean build")
      stampedClean.all(filter).value

    } else Def.task {
      streams.value.log.info(s"No changes to ${triggerFile} not running clean")
    }

  }

  override val settings = Seq(
    cacheDirectory in triggeredClean <<= cacheDirectory( _/"clean_trigger" ),
    cleanTriggerFile <<= baseDirectory( _/"clean_trigger" ),
    triggeredClean := {
      dynamicClean.value
    },
    stampedClean := { stampedCleanImpl.value },
    (Keys.compile in Compile in ThisProject) <<= (Keys.compile in Compile in ThisProject) dependsOn (triggeredClean)
  )
}
