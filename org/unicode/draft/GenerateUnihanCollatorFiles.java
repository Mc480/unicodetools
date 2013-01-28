package org.unicode.draft;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ibm.icu.dev.util.BagFormatter;

public class GenerateUnihanCollatorFiles {

    static final String INPUT_DIRECTORY = CldrUtility.COMMON_DIRECTORY + "/collation/";
    static final String OUTPUT_DIRECTORY = CldrUtility.GEN_DIRECTORY + "cldr-tmp/dropbox/han";
    static final String OUTPUT_DIRECTORY_REPLACE = CldrUtility.GEN_DIRECTORY + "cldr-tmp/dropbox/han/replace";

    static final Pattern START_AUTOGEN = Pattern.compile(".*<!--\\s*START\\s*AUTOGENERATED\\s*(.*)\\s*-->.*");
    static final Pattern END_AUTOGEN = Pattern.compile(".*<!--\\s*END\\s*AUTOGENERATED\\s*(.*)\\s*-->.*");

    public static void main(String[] args) throws IOException {
        composeHanFiles("zh.xml");
        composeHanFiles("ja.xml");
        composeHanFiles("ko.xml");
    }

    private static void composeHanFiles(String fileName) throws IOException {
        PrintWriter newFile = BagFormatter.openUTF8Writer(GenerateUnihanCollatorFiles.OUTPUT_DIRECTORY_REPLACE, fileName);
        BufferedReader oldFile = BagFormatter.openUTF8Reader(GenerateUnihanCollatorFiles.INPUT_DIRECTORY, fileName);
        Matcher start_autogen = GenerateUnihanCollatorFiles.START_AUTOGEN.matcher("");
        Matcher end_autogen = GenerateUnihanCollatorFiles.END_AUTOGEN.matcher("");

        while (true) {

            // copy up to the first autogen comment, including the comment line
            String matchingLine = CldrUtility.copyUpTo(oldFile, start_autogen, newFile, true);
            newFile.flush();
            if (matchingLine == null) {
                break; // end of file
            }
            final String choice = start_autogen.group(1);
            String replacementFile = choice.toLowerCase(Locale.ENGLISH).trim()
            .replaceAll("\\s+", "_").replace("_long", "").replace("stroke", "strokeT")
            + ".XML";

            // copy the file to be inserted
            BufferedReader insertFile = BagFormatter.openUTF8Reader(GenerateUnihanCollatorFiles.OUTPUT_DIRECTORY, replacementFile);
            CldrUtility.copyUpTo(insertFile, (Matcher)null, newFile, true); // copy to end
            newFile.flush();
            insertFile.close();

            // skip to the end of the matching autogen comment
            matchingLine = CldrUtility.copyUpTo(oldFile, end_autogen, null, true);

            // check for matching comment
            if (matchingLine == null || !choice.equals(end_autogen.group(1))) {
                throw new IllegalArgumentException("Mismatched comments for autogeneration: " + choice + ", " + matchingLine);
            }
            newFile.println(matchingLine); // copy comment line
            newFile.flush();
        }
        oldFile.close();
        newFile.close();
    }
}

