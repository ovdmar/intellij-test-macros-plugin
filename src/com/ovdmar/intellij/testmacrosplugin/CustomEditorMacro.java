package com.ovdmar.intellij.testmacrosplugin;

import com.intellij.ide.macro.Macro;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

public abstract class CustomEditorMacro extends Macro {

    private final String myName;
    private final String myDescription;

     CustomEditorMacro(String name, String description) {
        this.myName = name;
        this.myDescription = description;
    }

    public String getName() {
        return this.myName;
    }

    public String getDescription() {
        return this.myDescription;
    }

    public final String expand(DataContext dataContext) {
        Editor editor = CommonDataKeys.EDITOR.getData(dataContext);
        VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(dataContext);
        if (file == null) return null;

        return editor != null ? this.expand(editor, file.getExtension()) : null;
    }

    @Nullable
    protected abstract String expand(Editor var1, String fileExtension);
}