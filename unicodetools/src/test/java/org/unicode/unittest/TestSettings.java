package org.unicode.unittest;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.ibm.icu.util.VersionInfo;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.unicode.cldr.util.CLDRPaths;

import org.unicode.text.utility.Settings;
import org.unicode.text.utility.Utility;

// import com.ibm.icu.dev.test.TestFmwk;
import com.ibm.icu.util.VersionInfo;

public class TestSettings extends TestFmwkMinusMinus{

    static final Set<String> Settings_to_skip = new HashSet<>(Arrays.asList(
        "latestVersion",
        "lastVersion"
    ));

    static final Set<String> CLDRPaths_to_skip = new HashSet<>(Arrays.asList(
            "COMPARE_PROGRAM",
            "UNICODE_VERSION",
            "COMMON_SUBDIR",
            "CASING_SUBDIR",
            "VALIDITY_SUBDIR",
            "VALIDITY_SUBDIR",
            "ANNOTATIONS_DERIVED_SUBDIR",
            "COLLATION_SUBDIR",
            "RBNF_SUBDIR",
            "TRANSFORMS_SUBDIR",
            "MAIN_SUBDIR",
            "SUBDIVISIONS_SUBDIR",
            "ANNOTATIONS_SUBDIR",
            "BIRTH_DATA_DIR" // had a calculation err
    ));

    @Disabled("dir issue")
    @Test
    public void TestSettingsDirs() throws IllegalArgumentException, IllegalAccessException {
        checkDirs(Settings.class, Settings_to_skip);
    }

    @Disabled("Broken")
    public void TestCLDRDirs() throws IllegalAccessException {
        // SRL: COMMON_DIR etc is not going to work here.
        checkDirs(CLDRPaths.class, CLDRPaths_to_skip);
//        for (String dir : TO_TEST) {
//            File file = new File(dir);
//            //assertTrue(++i + ") " + dir + " exists", file.exists());
//            assertTrue(++i + ") " + dir + " is dir", file.isDirectory());
//        }
    }

    private void checkDirs(Class<?> class1, Set<String> skips) throws IllegalAccessException {
        String[] fullClassNames = class1.getName().split("\\.");
        String className = fullClassNames[fullClassNames.length-1];
        for (Field field : class1.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (java.lang.reflect.Modifier.isStatic(modifiers)
                    && java.lang.reflect.Modifier.isPublic(modifiers)) {
                String name = field.getName();
                if (skips.contains(name)) {
                    continue;
                }
                Class<?> fieldClass = field.getType();
                if (fieldClass != String.class) {
                    continue;
                }
                Object fieldGet = field.get(null);
                if (fieldGet == null) {
                    errln(className + "." + name + " is null");
                    continue;
                }
                String dir = fieldGet.toString();
                File file = new File(dir);
                assertTrue(className + "." + name + ", " + dir + " is dir", file.isDirectory());
            }
        }
    }

    public void TestDataDirVersion() {
        assertEquals("Emoji 1.2", "1.2",
                Settings.UnicodeTools.DataDir.EMOJI.versionToString(VersionInfo.getInstance(1, 2, 3, 4)));
        assertEquals("UCD 1.2.3", "1.2.3",
                Settings.UnicodeTools.DataDir.UCD.versionToString(VersionInfo.getInstance(1, 2, 3, 4)));
        assertEquals("Emoji 1.0", "1.0",
                Settings.UnicodeTools.DataDir.EMOJI.versionToString(VersionInfo.getInstance(1, 0, 0, 0)));
        assertEquals("UCD 1.0.0", "1.0.0",
                Settings.UnicodeTools.DataDir.UCD.versionToString(VersionInfo.getInstance(1, 0, 0, 0)));
    }

    public void TestDataDirPath() {
        assertEquals("Emoji 1.2", Settings.UnicodeTools.DATA_PATH.resolve("emoji/1.2"),
                Settings.UnicodeTools.DataDir.EMOJI.asPath(VersionInfo.getInstance(1, 2, 3, 4)));
        assertEquals("UCD 1.2.3", Settings.UnicodeTools.DATA_PATH.resolve("ucd/1.2.3-Update"),
                Settings.UnicodeTools.DataDir.UCD.asPath(VersionInfo.getInstance(1, 2, 3, 4)));
        assertEquals("Security 1.0.0", Settings.UnicodeTools.DATA_PATH.resolve("security/1.0.0"),
                Settings.UnicodeTools.DataDir.SECURITY.asPath(VersionInfo.getInstance(1, 0, 0, 0)));
        assertEquals("Emoji 1.0", Settings.UnicodeTools.DATA_PATH.resolve("emoji/1.0"),
                Settings.UnicodeTools.DataDir.EMOJI.asPath(VersionInfo.getInstance(1, 0, 0, 0)));
        assertEquals("UCD 1.0.0", Settings.UnicodeTools.DATA_PATH.resolve("ucd/1.0.0-Update"),
                Settings.UnicodeTools.DataDir.UCD.asPath(VersionInfo.getInstance(1, 0, 0, 0)));
    }

}
