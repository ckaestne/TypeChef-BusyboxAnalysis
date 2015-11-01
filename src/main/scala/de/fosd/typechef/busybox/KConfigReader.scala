package de.fosd.typechef.busybox

import java.io.File

/**
 * adhoc reader for KConfig (busybox subset)
 *
 * expects three parameters
 * - directory (which contains a Config.in file)
 * - target file in which the feature model is stored
 * - target file in which the generated header file is stored
 *
 */

object KConfigReader extends App {

    if (args.size < 1) {
        println("expected four parameters\n" +
            " - directory (which contains a Config.in file)\n" +
            " - target file in which the feature model is stored\n" +
            " - target file in which the generated header file is stored\n" +
            " - target file which will contain a list of all features")
        sys.exit();
    }

    if (!new File(args(0)).exists || !new File(args(0)).isDirectory)
        throw new RuntimeException(args(0) + " is not a directory")


    val path = args(0) //"S:\\ARCHIVE\\kos\\share\\TypeChef\\busybox\\busybox-1.18.5\\"

    var config = io.Source.fromFile(path + "Config.in").getLines().toList

    var outFeatureModel = ""
    var outHeader = "";


    var flag = ""
    var skipHelp = false
    var flagType = ""
    var flagDep = ""
    var groupDep = ""
    var inChoiceHeader = false
    var menuStack = List[String]()

    var features = List[String]()
    var choiceAlternatives: List[String] = Nil

    var version = io.Source.fromFile(path + "Makefile").getLines().toList
    var prefix = "IF"
    var prefixNegation = "IF_NOT"

    def processConfig() {
        if (flag == "") return;
        if (flagType == "bool") {
            println(dir + "/" + flag /*+ " => " + flagDep*/)

           prefix = "IF"
           prefixNegation = "IF_NOT"

           // Version 1.14 and previous versions use other makros
           if (!version.isEmpty && version.head.substring(0, 11) == "VERSION = 1") {
                version = version.tail;

                if (!version.isEmpty && ((version.head.length() == 15 &&
                                          version.head.substring(13, 14) == "1" && version.head.substring(14, 15) < "5")
                                     || (version.head.length() <= 14 ))) {
                    prefix = "USE"
                    prefixNegation = "SKIP"
                }
            }

            outHeader += "#ifdef CONFIG_" + flag + "\n" +
                "   #define ENABLE_" + flag + " 1\n" +
                "   #define " + prefix + "_" + flag + "(...) __VA_ARGS__\n" +
                "   #define " + prefixNegation + "_" + flag + "(...)\n" +
                "#else\n" +
                "   #define ENABLE_" + flag + " 0\n" +
                "   #define " + prefixNegation + "_" + flag + "(...) __VA_ARGS__\n" +
                "   #define " + prefix + "_" + flag + "(...)\n" +
                "#endif\n\n"

            if (flagDep.trim.startsWith("&&")) flagDep = flagDep.drop(2)
            if (groupDep.trim.startsWith("&&")) groupDep = groupDep.drop(2)

            if (!flagDep.trim.isEmpty)
                outFeatureModel += "defined(CONFIG_" + flag + ") => " + flagDep.replaceAll("\\w+", "defined(CONFIG_$0)") + "\n"
            if (!groupDep.trim.isEmpty)
                outFeatureModel += "defined(CONFIG_" + flag + ") => " + groupDep.replaceAll("\\w+", "defined(CONFIG_$0)") + "\n"

            features = flag :: features
        }
        choiceAlternatives = flag :: choiceAlternatives
        flag = ""
        skipHelp = false
        flagType = ""
        flagDep = ""
        version = io.Source.fromFile(path + "Makefile").getLines().toList
    }
    def processChoice() {
        if (choiceAlternatives.isEmpty) return;
        outFeatureModel += choiceAlternatives.map("defined(CONFIG_" + _ + ")").mkString("oneOf(", ",", ")\n")
    }

    var dir = ""

    while (!config.isEmpty) {
        val line = config.head
        config = config.tail

        var skip = false
        if (line.trim == "" || line.trim.startsWith("#") || line.trim.startsWith("comment") || line.trim.startsWith("mainmenu")) skip = true

        if (!skip && line.startsWith("config")) {
            inChoiceHeader = false
            processConfig()

            flag = line.drop(7).trim
        }
        if (!skip && line.startsWith("menu")) {
            //            println("<menu " + line.drop(5) + ">")
            menuStack = line.drop(5) :: menuStack
        }
        if (!skip && line.startsWith("endmenu")) {
            //            println("</menu " + menuStack(0) + ">")
            menuStack = menuStack.tail
        }

        if (!skip && line.startsWith("source")) {
            processConfig()
            var file = path + line.drop(7)
            dir = line.drop(7).take(line.drop(7).lastIndexOf("/"))
            config =
                io.Source.fromFile(file).getLines().toList ++ config
        }

        if (!skip && line.trim() == ("choice")) {
            processConfig
            choiceAlternatives = Nil
            inChoiceHeader = true
        }

        if (!skip && line.trim() == ("endchoice")) {
            processConfig
            processChoice
            groupDep = ""
            inChoiceHeader = false
        }

        if (!skip && !skipHelp && flag != "" && line.trim() == ("help"))
            skipHelp = true

        if (!skip && !skipHelp && flag != "" && line.trim().startsWith("bool"))
            flagType = "bool"

        if (!skip && !skipHelp && line.trim().startsWith("depends on")) {
            var v = line.trim.drop(11)
            //remove trailing comments
            if (v.indexOf('#') >= 0)
                v = v.take(v.indexOf('#'))
            if (flag != "")
                flagDep = flagDep + "&&(" + v + ")"
            if (inChoiceHeader)
                groupDep = groupDep + "&&(" + v + ")"
        }

        if (!skip && !skipHelp && line.trim().startsWith("select")) {
            var v = line.trim.drop(7)
            //remove trailing comments
            if (v.indexOf('#') >= 0)
                v = v.take(v.indexOf('#'))
            if (flag != "")
                flagDep = flagDep + "&&(" + v + ")"
            if (inChoiceHeader)
                groupDep = groupDep + "&&(" + v + ")"
        }

    }

    processConfig()

    assert(menuStack.isEmpty)

    println(features.size + " - " + features.toSet.size)

    if (args.size > 1) {
        val p = new java.io.PrintWriter(args(1))
        try {
            p.write(outFeatureModel)
        } finally {
            p.close()
        }
    }
    if (args.size > 2) {
        val p = new java.io.PrintWriter(args(2))
        try {
            p.write(outHeader)
        } finally {
            p.close()
        }
    }
    if (args.size > 3) {
        val p = new java.io.PrintWriter(args(3))
        try {
            for (feature <- features) p.write(feature + "\n")
        } finally {
            p.close()
        }
    }


}

