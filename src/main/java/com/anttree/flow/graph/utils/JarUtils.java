package com.anttree.flow.graph.utils;

import com.anttree.flow.graph.model.Ext;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.*;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class JarUtils {

    public static HashMap<String, ClassNode> getEntries(
            String inputFile
    ) throws IOException {
        HashMap<String, ClassNode> classes = new HashMap<>();
        FileInputStream fileInputStream = new FileInputStream(inputFile);
        ZipInputStream zipInputStream = new ZipInputStream(
                new BufferedInputStream(fileInputStream)
        );

        ZipEntry entry;
        while ((entry = zipInputStream.getNextEntry()) != null) {
            if (entry.isDirectory()) {
                continue;
            }

            byte[] entryData = readEntryFromJar(zipInputStream);
            String entryName = entry.getName();

            if (!entryName.endsWith(Ext.CLASS)) {
                continue;
            }

            try {
                ClassReader classReader = new ClassReader(entryData);
                ClassNode classNode = new ClassNode();
                classReader.accept(classNode, ClassReader.EXPAND_FRAMES);

                classes.put(entryName, classNode);
            } catch (Exception e) {
                //Ignore the failed classes if they are preserved
                e.printStackTrace();
            }
        }

        zipInputStream.close();

        return classes;
    }

    public static byte[] readEntryFromJar(ZipInputStream inJar) throws IOException {
        byte[] data = new byte[4096];
        ByteArrayOutputStream entryBuffer = new ByteArrayOutputStream();

        int readLength;
        do {
            readLength = inJar.read(data);
            if (readLength > 0) {
                entryBuffer.write(data, 0, readLength);
            }
        } while (readLength != -1);

        return entryBuffer.toByteArray();
    }

    public static boolean isDefaultClassName(String className) {
        return className.startsWith("java/") ||
                className.startsWith("javax/") ||
                className.startsWith("android/") ||
                className.startsWith("androidx/") ||
                className.startsWith("kotlin/") ||
                className.startsWith("com/google/") ||
                className.startsWith("org/hamcrest/") ||
                className.startsWith("org/junit/");
    }

}
