name := "TypeChef Busybox Analysis"

version := "0.3.8"

scalaVersion := "2.11.4"

libraryDependencies += "de.fosd.typechef" %% "frontend" % "0.3.7"

libraryDependencies += "de.fosd.typechef" % "javabdd_repackaged" % "1.0b2"

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
