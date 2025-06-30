package com.anttree.flow.graph.options;

import joptsimple.OptionParser;

public class ParserAssembler {

    public static void run(OptionParser parser) {
        //+------------------------------------------------------------------------------------------+
        // Required Options (Order-sensitive)
        //+------------------------------------------------------------------------------------------+
        parser.accepts("mode")
                .withRequiredArg()
                .ofType(String.class)
                .required()
                .describedAs("mode for CFG generation.\n"
                        + "- \"class\" (or \"c\") will draw a intra-class call flow graph.\n"
                        + "field, method calls (and access) will be depicted in a graph.\n"
                        + "- \"jar\" (or \"j\") will draw a inter-class call flow graph.\n"
                        + "classes will be the only node type depicted in a graph, \n"
                        + "since full relation graph is too big to be drawn.");

        parser.accepts("in")
                .withRequiredArg()
                .ofType(String.class)
                .required()
                .describedAs("define the input file. \n"
                        + "currently this only takes .jar file as an input.");

        parser.accepts("out")
                .withRequiredArg()
                .ofType(String.class)
                .required()
                .describedAs("define the output file path."
                        + "output file path should be fully qualified path with extension (.png)");

        //+------------------------------------------------------------------------------------------+
        // Optionals
        //+------------------------------------------------------------------------------------------+
        parser.accepts("target")
                .withOptionalArg()
                .ofType(String.class)
                .defaultsTo("")
                .describedAs("target to be drawn.\n" +
                        "in class mode, this should be the class name (ex. com/package/name/ClassName)\n" +
                        "otherwise, this can be multiple class name or package name, comma separated.");

        parser.accepts("anonymous")
                .withOptionalArg()
                .ofType(Boolean.class)
                .defaultsTo(false)
                .describedAs("draw anonymous class node or not.\n"
                        + "if true, all anonymous classes will be included in a flow graph.\n"
                        + "set this flag to false to minify the graph size.");

        //+------------------------------------------------------------------------------------------+
        // Etc
        //+------------------------------------------------------------------------------------------+
        parser.accepts("help").forHelp();
        parser.accepts("version").forHelp();
    }

}
