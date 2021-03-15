package com.ovdmar.intellij.testmacrosplugin;

import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DevcliTestMacro returns a string that can be used to run tests from the current file using devcli:
 * The string has the following format: %s.%s, where the first string is the suite name (or the test that starts the
 * suite for Go Tests) and the second one is the selected text if not empty or the method in which the caret is enclosed
 * if the selected text was empty
 */
public class DevcliTestMacro extends CustomEditorMacro {

    private final Pattern goTestPattern = Pattern.compile("func \\(suite .*\\) (Test.*)\\(\\) \\{");
    private final Pattern pyTestPattern = Pattern.compile("def (test.*)\\(self.*\\):");
    private final Pattern goSuitePattern = Pattern.compile(".*func (.*)\\(t \\*testing\\.T\\) \\{.*");
    private final Pattern pySuitePattern = Pattern.compile(".*class (.*)\\(.*\\):");
    private final String goTestDef = "func (suite";
    private final String pyTestDef = "def test";

    public DevcliTestMacro() {
        super("DevcliTestFormat", "Returns the current test from editor to be run with External Tools");
    }

    @Nullable
    @Override
    protected String expand(Editor editor, String fileExt) {
        Pattern suitePattern;
        Pattern testPattern;
        String testDef;
        switch (fileExt) {
            case "py":
                suitePattern = pySuitePattern;
                testPattern = pyTestPattern;
                testDef = pyTestDef;
                break;
            case "go":
                suitePattern = goSuitePattern;
                testPattern = goTestPattern;
                testDef = goTestDef;
                break;
            default:
                return "Unsupported file type";
        }

        try {
            String crtFileText = editor.getDocument().getText();
            Matcher suiteMatcher = matchText(crtFileText, suitePattern);
            String suite = suiteMatcher.group(1);

            String test;
            String selectedTest = editor.getSelectionModel().getSelectedText();
            if (selectedTest != null && !selectedTest.equals("")) {
                test = selectedTest;
            } else {
                int caretOffset = editor.getCaretModel().getOffset();
                String textBeforeCaret = crtFileText.substring(0, caretOffset);

                int testStartOffset = textBeforeCaret.lastIndexOf(testDef);
                String testString = textBeforeCaret.substring(testStartOffset);

                Matcher testMatcher = matchText(testString, testPattern);
                test = extractLastGroup(testMatcher);
            }

            return suite + "." + test;
        } catch (Exception e) {
            return "";
        }
    }

    private String extractLastGroup(Matcher matcher) {
        int groupCount = matcher.groupCount();
        return matcher.group(groupCount);
    }

    private Matcher matchText(String text, Pattern pattern) throws Exception {
        Matcher matcher = pattern.matcher(text);

        boolean result = matcher.find();
        if (!result) {
            throw new Exception();
        }
        return matcher;
    }
}
