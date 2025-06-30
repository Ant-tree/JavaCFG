package com.anttree.flow.graph;

import com.anttree.flow.graph.analysis.ClassAnalyzer;
import com.anttree.flow.graph.analysis.JarAnalyzer;
import com.anttree.flow.graph.canvas.CFGOptions;
import com.anttree.flow.graph.canvas.Graph;
import com.anttree.flow.graph.model.Constants;
import com.anttree.flow.graph.model.Reference;
import com.anttree.flow.graph.options.ParserAssembler;
import com.anttree.flow.graph.options.PrettyHelpFormatter;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.*;
import java.util.*;

public class ControlFlowGraph {

    public static final String VERSION = "1.0.0.0";

    public static void main(String[] args) {
        if (args.length < 1) {
            throw new RuntimeException("");
        }

        OptionParser parser = new OptionParser();
        ParserAssembler.run(parser);

        try {
            OptionSet options = parser.parse(args);

            if (options.has("help")) {
                System.out.println(VERSION);
                parser.formatHelpWith(new PrettyHelpFormatter());
                parser.printHelpOn(System.out);
                return;
            } else if (options.has("version")) {
                System.out.println(VERSION);
                return;
            }

            String input = options.has("in")
                    ? (String) options.valueOf("in")
                    : null;

            String output = options.has("out")
                    ? (String) options.valueOf("out")
                    : null;

            String mode = options.has("mode")
                    ? (String) options.valueOf("mode")
                    : null;

            String target = options.has("target")
                    ? (String) options.valueOf("target")
                    : null;

            boolean omitAnonymousClasses = options.has("anonymous")
                    ? (Boolean) options.valueOf("anonymous") : false;

            if (mode == null) {
                parser.formatHelpWith(new PrettyHelpFormatter());
                parser.printHelpOn(System.out);
                return;
            }

            CFGOptions cfgOptions = new CFGOptions(omitAnonymousClasses);

            if (mode.equalsIgnoreCase(Constants.MODE_CLASS) ||
                mode.equalsIgnoreCase(Constants.MODE_CLASS_FULL)
            ) {
                if (target == null || target.isEmpty()) {
                    System.err.println("--target class must be specified when mode is class");
                }
                processOnClass(output, input, target);
                return;
            }

            if (mode.equalsIgnoreCase(Constants.MODE_JAR) ||
                mode.equalsIgnoreCase(Constants.MODE_JAR_FULL)
            ) {
                processOnJar(output, input, target, cfgOptions);
                return;
            }

            parser.printHelpOn(System.out);

        } catch (OptionException e) {
            System.err.println(e.getMessage() + " (Tip: try --help)");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void processOnClass(
            String output,
            String target,
            String targetClass
    ) {
        Set<Reference> references;
        if ((references = new ClassAnalyzer(target).run(
                targetClass
        )) == null) {
            throw new RuntimeException("Failed to run analyzer");
        }
        Graph.draw(targetClass, references, output);
    }

    private static void processOnJar(
            String output,
            String target,
            String targetPackage,
            CFGOptions CFGOptions
    ) {
        Set<Reference> references;
        if ((references = new JarAnalyzer(target).run(
                targetPackage
        )) == null) {
            throw new RuntimeException("Failed to run analyzer");
        }

        Graph.drawClassOnly(new File(target).getName()
                , references
                , CFGOptions
                , output);
    }

}

