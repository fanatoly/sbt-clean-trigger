# SBT Clean Trigger #

-------------------------------------------------------------------------------

## What is? ##

Periodically, incremental compilation in SBT lets us down. We run into
a bug in one of the plugins or in the incremental compiler itself that
requires an `sbt clean`. This plugin allows you to encode the need for
a clean build within the source history of your codebase.

As an example, imagine that you've run into this
[bug](https://github.com/sbt/sbt-protobuf/pull/20). Instead of just
running `sbt clean` you can `touch clean_trigger`. Next time you
compile, your own build will be cleaned first. Additionally, when you
push out your changes to SCM, your collaborators will be forced to do
a clean build as well.

## Usage ##

In project/plugins.sbt

```scala
addSbtPlugin("io.github.fanatoly" % "sbt-clean-trigger" % "0.1-SNAPSHOT") 
```

Subsequently touching or changing `clean_trigger` will trigger a clean build

# Customization #

You can change the name of the clean trigger file per project by doing

```scala
cleanTriggerFile in triggeredClean := filename
```

## TODO ##

The plugin is very much in alpha. There are a couple of things that could make it better

    - Better multi module support. Currently, a clean trigger will work on the current project or the aggregated projects e.g. if you have multiple modules aggregated by a root project `touching` the root `clean_trigger` file will cause clean to run on all the projects. In the same build, touching a non-root clean trigger will only cause that project's build directory to be cleaned
	- Quieter logging - the plugin is pretty verbose right now. It is new and likely to have bugs.
