package com.ovdmar.intellij.testmacrosplugin;

import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestSuiteMacro extends CustomEditorMacro {

    private Pattern goSuitePattern = Pattern.compile(".*func (.*)\\(t \\*testing\\.T\\) \\{.*");
    private Pattern pySuitePattern = Pattern.compile(".*class (.*)\\(.*\\):");

    public TestSuiteMacro() {
        super("TestSuiteMacro", "This macro can be used to inject GO tests suite struct");
    }

    @Nullable
    @Override
    protected String expand(Editor editor, String fileExt) {
        String crtFileText = editor.getDocument().getText();

        switch (fileExt) {
            case "py":
                return extractGroup(crtFileText, pySuitePattern, 1);
            case "go":
                return extractGroup(crtFileText, goSuitePattern, 1);
            default:
                return "Unsupported file type";
        }
    }

    private String extractGroup(String text, Pattern pattern, int groupNr) {
        Matcher matcher = pattern.matcher(text);

        boolean result = matcher.find();
        if (!result) {
            return "";
        }

        return matcher.group(groupNr);
    }
}