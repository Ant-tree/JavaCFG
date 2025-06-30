package com.anttree.flow.graph.utils;

import com.anttree.flow.graph.model.Ext;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NodeUtils {

    public static String normalizeClassName(String className) {
        String normalizedClassName = className.trim();
        if (normalizedClassName.startsWith("L")) {
            normalizedClassName = normalizedClassName.substring(1);
        }
        if (normalizedClassName.endsWith(";")) {
            normalizedClassName = normalizedClassName.substring(0,
                    normalizedClassName.length() - 1
            );
        }
        if (normalizedClassName.endsWith(Ext.CLASS)) {
            normalizedClassName = normalizedClassName.substring(0,
                    normalizedClassName.length() - Ext.CLASS.length()
            );
        }
        return normalizedClassName.replace(".", "/");
    }

    public static Set<String> getGenericTypesInSignature(String signature) {
        Set<String> classNames = new HashSet<>();
        if (signature == null || signature.isEmpty()) {
            return classNames;
        }
        Pattern pattern = Pattern.compile("<([^>]+)>");
        Matcher matcher = pattern.matcher(signature);

        while (matcher.find()) {
            String genericType = matcher.group(1);
            classNames.add(normalizeClassName(genericType));
        }
        return classNames;
    }

    public static Set<String> getClassNamesFromMethodDescriptor(String descriptor) {
        Set<String> classNames = new HashSet<>();
        int paramStart = descriptor.indexOf('(');
        int paramEnd = descriptor.indexOf(')');

        if (paramStart >= 0 && paramEnd >= 0 && paramStart < paramEnd) {
            // Extract the parameter part of the descriptor
            String parameters = descriptor.substring(paramStart + 1, paramEnd);
            // Extract class names from parameters
            extractClassNames(parameters, classNames);
        }

        // Extract class name from return type
        String returnType = descriptor.substring(paramEnd + 1);
        extractClassNames(returnType, classNames);

        return classNames;
    }

    private static void extractClassNames(String str, Set<String> classNames) {
        int startIndex = 0;
        while (startIndex < str.length()) {
            char ch = str.charAt(startIndex);
            if (ch != 'L') {
                startIndex++;
                continue;
            }
            int endIndex = str.indexOf(';', startIndex);
            if (endIndex >= 0) {
                String className = str.substring(startIndex + 1, endIndex);
                classNames.add(className);
                startIndex = endIndex + 1;
            } else {
                break;
            }
        }
    }

    public static String omitAnonymousClass(String className) {
        if (className == null || className.isEmpty()) {
            return className;
        }
        // Check if the last segment is an anonymous class
        // For example, if the class name is "com/example/MyClass$1"
        // If the last word after dollar sign is a digit, it's an anonymous class
        String originClassName = className;
        while (originClassName.contains("$") && originClassName.matches(".*\\$\\d+")) {
            originClassName = originClassName.substring(0, originClassName.lastIndexOf("$"));
        }
        return originClassName;
    }

}
