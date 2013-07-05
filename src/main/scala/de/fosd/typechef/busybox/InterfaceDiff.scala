package de.fosd.typechef.busybox

import java.io.File
import de.fosd.typechef.typesystem.linker.{CSignature, SystemLinker, InterfaceWriter}

/**
 * compares two interface files
 *
 * textual diffs often fail because they do not recognize equivalences of formulas
 */

object InterfaceDiff extends App {

    if (args.size != 2) {
        println("expecting two .interface files as parameter")
    } else if (!new File(args(0)).exists()) {
	println("no previous file exists")
    } else {

        val reader = new InterfaceWriter() {}

        val ainterface = reader.readInterface(new File(args(0)))
        val binterface = reader.readInterface(new File(args(1)))

        if (!(ainterface.featureModel equivalentTo binterface.featureModel))
            println("fm change: \n<  %s\n>  %s".format(ainterface.featureModel.toString(), binterface.featureModel.toString()))

        //groups imports/exports by name+type
        def toSet(aa:Seq[CSignature]):Set[CSignature] =
            aa.groupBy(x=>(x.name,x.ctype)).values.map(x=>x.reduce((a,b)=>CSignature(a.name,a.ctype,a.fexpr or b.fexpr, a.pos++b.pos))).toSet

        def compareSigs(aa: Seq[CSignature], bb: Seq[CSignature]) {
            val a=toSet(aa)
            val b=toSet(bb)

            for (imp <- a.toList.sortBy(_.name)) {
                val bimp = b.filter(x => x.name == imp.name && x.ctype == imp.ctype)
                if (bimp.isEmpty)
                    println("<< " + imp) //removed
                else if (!(imp.fexpr equivalentTo bimp.map(_.fexpr).reduce(_ or _))) {
                    //changed FExpr
                    println("<  " + imp)
                    for (bi <- bimp)
                        println(">  " + bi)
                }
            }
            for (imp <- b) {
                val aimp = a.filter(x => x.name == imp.name && x.ctype == imp.ctype)
                if (aimp.isEmpty)
                    println(">> " + imp) //new


            }
        }

        println("imports:")
        compareSigs(ainterface.imports, binterface.imports)
        println("exports:")
        compareSigs(ainterface.exports, binterface.exports)
        println(".")

    }
}

