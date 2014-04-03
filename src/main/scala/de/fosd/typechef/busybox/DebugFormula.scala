package de.fosd.typechef.busybox

import de.fosd.typechef.featureexpr.{FeatureExprParser, FeatureExprFactory}
import de.fosd.typechef.featureexpr.bdd.BDDFeatureExpr

/**
 * simple formula debugger which prints the formula in DNF
 *
 * every solution gets its own line, the lines are sorted
 */
object DebugFormula extends App {

    FeatureExprFactory.setDefault(FeatureExprFactory.bdd)

    val fexpr =
        (if (args.length > 0)
            new FeatureExprParser().parse(args.mkString(" "))
        else
            new FeatureExprParser().parse(io.Source.stdin.reader())).asInstanceOf[BDDFeatureExpr]


    val dnf=fexpr.toStringDNF
    for (clause <- dnf.split("\\|").map(_.trim)) {
        println(clause.split("&").map(_.trim).sorted.mkString(" & "))
    }


}
