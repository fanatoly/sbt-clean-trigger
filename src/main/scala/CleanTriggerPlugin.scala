package io.github.fanatoly

import sbt._
import sbt.Def._
import sbt.Keys._

object CleanTriggerPlugin extends Plugin{
  val cleanTriggerFile = 
    Def.settingKey[File]("File used to determine whether clean should be run")

  val triggeredClean =
    Def.taskKey[Unit]("Task that executes the clean task based on whether the trigger is updated")

  private val dynamicClean = Def.taskDyn {
    val cacheStamp = Difference.inputs(
      (cacheDirectory in triggeredClean).value,
      FilesInfo.lastModified
    )

    val shouldClean = cacheStamp(Set(cleanTriggerFile.value)){ _.modified.nonEmpty }

    if(shouldClean) Def.task{
      streams.value.log.info(s"A change to ${cleanTriggerFile.value} has triggered a clean build")
      clean.value
      cacheStamp(Set(cleanTriggerFile.value)){ _ => }
    } else Def.task {}

  }

  override val settings = Seq(
    cacheDirectory in triggeredClean <<= cacheDirectory( _/"clean_trigger" ),
    cleanTriggerFile <<= baseDirectory( _/"clean_trigger" ),
    triggeredClean := {
      dynamicClean.value
    },
    (Keys.compile) <<= (Keys.compile in Compile) dependsOn (triggeredClean)
  )
}
