organization := "io.github.fanatoly"

name := "sbt-clean-trigger"

sbtPlugin := true

ScriptedPlugin.scriptedSettings

scriptedBufferLog := false

scriptedLaunchOpts <+= version( x => s"-Dplugin.version=${x}" )

