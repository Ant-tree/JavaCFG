package com.anttree.flow.graph.options;

import joptsimple.HelpFormatter;
import joptsimple.OptionDescriptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PrettyHelpFormatter implements HelpFormatter {

    private static final String INDENT              = "        ";
    private static final String INDENT_MINUS_ONE    = "       ";
    private static final String LINE_INDENT         = "\n" + INDENT;
    private static final int maxProcessorOptionLength = 20;

    @Override
    public String format(Map<String, ? extends OptionDescriptor> map) {
        String usage = ConsoleUtils.formatBox("Usage", false, Arrays.asList(
                "java -jar CFGGenerator.jar \\",
                "--mode     class",
                "--in       INPUT.jar \\",
                "--out      OUTPUT_DIR/output.png \\",
                "--target   com/package/name/ClassName \\"
        ));
        String header = usage + "\n\nOptions: (* for required) \n";
        String footer = "\n";

        List<String> required = new ArrayList<>();

        for (OptionDescriptor option : map.values()
                .stream()
                .filter(OptionDescriptor::isRequired)
                .collect(Collectors.toList())
        ) {
            beautifyRequired(option, required);
        }

        List<OptionDescriptor> optionLines = map.values().stream()
                .filter(d -> !d.isRequired() && !(
                        d.options().size() == 1 &&
                        d.options().get(0).equals("[arguments]")
                )).collect(Collectors.toList());

        String optionsDescription = optionLines.stream()
                .filter(d -> !d.isProcessorSwitch())
                .map(this::beautifyOptionals)
                .collect(Collectors.joining("\n"));

        return header + ConsoleUtils.formatBox(
                "REQUIRED",
                false,
                required
        ) + "\n"
                + "Optionals : \n"
                + optionsDescription
                + footer + "\n";
    }

    private static void beautifyRequired(OptionDescriptor option, List<String> required) {
        String optionDetails = "* " + option.options().stream()
                .map(o -> "--" + o)
                .collect(Collectors.joining(", "));
        required.add(optionDetails);

        if (!option.defaultValues().isEmpty()) {
            required.add(INDENT + "(default: " + option.defaultValues() + ")");
        }
        if (option.hasShortTypeIndicator()) {
            required.add(INDENT + "Type : [" + option.shortTypeIndicator() + "]");
        }
        Arrays.asList(option.argumentDescription().split("\n")).forEach(
                line -> required.add(INDENT + line)
        );
    }

    private String beautifyOptionals(OptionDescriptor option) {
        String optionDetails = option.options().stream()
                .map(o -> "--" + o)
                .collect(Collectors.joining(", "));

        optionDetails += option.shortTypeIndicatorStyled(LINE_INDENT);
        optionDetails += option.defaultValuesStyled(LINE_INDENT);

        String argumentDescription = String.join(
                LINE_INDENT,
                option.argumentDescription().split("\n")
        );

        return String.format("%s" + LINE_INDENT + "%s"
                , optionDetails
                , argumentDescription);
    }

    private String beautifyProcessors(OptionDescriptor option) {
        String optionDetails = "--" + option.options().get(0);

        int margins = Math.max(
                0,
                maxProcessorOptionLength - optionDetails.length()
        );
        String space = " ".repeat(margins);
        boolean isOn = (boolean) option.defaultValues().get(0);

        optionDetails = optionDetails + space
                + "[" + (isOn ? "ON" : "OFF") + "]"
                + (isOn ? INDENT : INDENT_MINUS_ONE)
                + option.argumentDescription();

        return optionDetails;
    }
}
