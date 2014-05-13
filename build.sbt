organization := "io.github.fanatoly"

name := "sbt-clean-trigger"

version := "0.2"

sbtPlugin := true

ScriptedPlugin.scriptedSettings

scriptedBufferLog := false

scriptedLaunchOpts <+= version( x => s"-Dplugin.version=${x}" )

S3Resolver.defaults

publishMavenStyle := false

publishTo := { 
  val prefix = if (isSnapshot.value) "snapshots" else "releases"
  Some(s3resolver.value("YielMo Public " + prefix, s3(prefix+".public.yieldmo")) withIvyPatterns)
}
