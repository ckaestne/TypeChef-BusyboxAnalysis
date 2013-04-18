package de.fosd.typechef.busybox

import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExprParser}
import de.fosd.typechef.featureexpr.sat.{SATFeatureModel, SATFeatureExpr}
import java.io.{OutputStreamWriter, PrintStream, File, FileWriter}
import FeatureExprFactory.sat._

/**
 * reads a feature expression from a file (parameter 1) and creates a dimacs file (parameter 2)
 */

object CreateDimacs extends App {
    if (args.length < 2) {
        println(
            """
              |Invalid parameters:
              | --cnf or --equicnf (mandatory) selecting between CNF and Equisatisfiable CNF transformations
              | input file (mandatory)
              | output file (optional) default is fm.dimacs
            """.stripMargin)
    }
    else {
        val isCNF = args(0) != "--equicnf"

        val inputFilename = args(1)
        assert(new File(inputFilename).exists(), "File " + inputFilename + " does not exist")
        val fexpr = new FeatureExprParser(FeatureExprFactory.sat).parseFile(inputFilename).asInstanceOf[SATFeatureExpr]


        val fm = SATFeatureModel.create(if (isCNF) fexpr else fexpr.toCnfEquiSat()).asInstanceOf[SATFeatureModel]

        val outputFilename = if (args.length < 3) "fm.dimacs" else args(2)
        val out = //new OutputStreamWriter())
            new FileWriter(outputFilename)

        for ((v, i) <- fm.variables)
            out.write("c " + i + " " + (if (v.startsWith("CONFIG_")) v.drop(7) else "$" + v) + "\n")

        out.write("p cnf " + fm.variables.size + " " + fm.clauses.size() + "\n")

        var i = 0
        while (i < fm.clauses.size) {
            val c = fm.clauses.get(i)
            val vi = c.iterator()
            while (vi.hasNext)
                out.write(vi.next + " ")
            out.write("0\n")
            i = i + 1
        }

        out.close()

        println("wrote .dimacs.")

    }
}
