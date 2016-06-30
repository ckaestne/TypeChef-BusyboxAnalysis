name := "TypeChef Busybox Analysis"

version := "0.3.7"

scalaVersion := "2.11.4"

organization := "de.fosd.typechef"


libraryDependencies += "com.github.scopt" %% "scopt" % "3.2.0"

libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4"

libraryDependencies += "de.fosd.typechef" %% "frontend" % "0.3.7"

TaskKey[File]("mkrun") <<= (baseDirectory, fullClasspath in Runtime, mainClass in Runtime) map { (base, cp, main) =>
  val template = """#!/bin/sh
java -ea -Xmx2G -Xms128m -Xss10m -classpath "%s" %s "$@"
"""
  val mainStr = ""
  val contents = template.format(cp.files.absString, mainStr)
  val out = base / "run.sh"
  IO.write(out, contents)
  out.setExecutable(true)
  out
}
