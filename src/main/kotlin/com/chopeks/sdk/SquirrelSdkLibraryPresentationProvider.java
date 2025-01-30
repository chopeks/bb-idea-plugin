package com.chopeks.sdk;

import com.chopeks.SquirrelIcons;
import com.chopeks.util.SquirrelConstants;
import com.intellij.openapi.roots.libraries.DummyLibraryProperties;
import com.intellij.openapi.roots.libraries.LibraryKind;
import com.intellij.openapi.roots.libraries.LibraryPresentationProvider;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class SquirrelSdkLibraryPresentationProvider extends LibraryPresentationProvider<DummyLibraryProperties> {
    private static final LibraryKind KIND = LibraryKind.create(SquirrelConstants.SQUIRREL);

    public SquirrelSdkLibraryPresentationProvider() {
        super(KIND);
    }

    @Nullable
    public Icon getIcon() {
        return SquirrelIcons.NUT_FILE;
    }

    @Nullable
    public DummyLibraryProperties detect(@NotNull List<VirtualFile> classesRoots) {
        for (VirtualFile root : classesRoots) {
            if (SquirrelSdkService.isSquirrelSdkLibRoot(root)) {
                return DummyLibraryProperties.INSTANCE;
            }
        }
        return null;
    }
}
