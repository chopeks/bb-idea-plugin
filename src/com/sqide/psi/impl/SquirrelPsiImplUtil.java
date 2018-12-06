package com.sqide.psi.impl;

import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.FileBasedIndex;
import com.sqide.SquirrelFileType;
import com.sqide.psi.*;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class SquirrelPsiImplUtil {
    private static ConcurrentHashMap<String, Collection<VirtualFile>> projectFiles = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<SquirrelId, SquirrelFunctionDeclarationPsiReferenceBase> lookups = new ConcurrentHashMap<>();

    public static PsiReference getReference(SquirrelIncludeDeclaration includeElement) {
        String includeLocation = includeElement.getString().getText().replaceAll("\"", "");
        String thisFile = includeElement.getContainingFile().getVirtualFile().getParent().getPath();
        Path resolve = Paths.get(thisFile).resolve(includeLocation).toAbsolutePath().normalize();

        VirtualFile file = LocalFileSystem.getInstance().findFileByIoFile(resolve.toFile());
        if (file == null) {
            return null;
        } else {
            PsiFile file1 = PsiManager.getInstance(includeElement.getProject()).findFile(file);

            return new SquirrelFilePsiReferenceBase(includeElement, file1);
        }
    }

    public static PsiReference getReference(SquirrelStringLiteral element) {
        PsiElement statement = PsiTreeUtil.findFirstParent(element, elem -> elem instanceof SquirrelExpressionStatement);
        if (statement == null) {
            return null;
        }

        String statementText = statement.getText();
        if (!statementText.startsWith("device.send") && !statementText.startsWith("agent.send") && !statementText.startsWith("device.on") && !statementText.startsWith("agent.on")) {
            return null;
        }


        String projectFilePath = Objects.requireNonNull(element.getProject().getProjectFilePath()) + false;

        if (!projectFiles.containsKey(projectFilePath)) {
            Collection<VirtualFile> containingFiles = FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, SquirrelFileType.INSTANCE,
                    GlobalSearchScope.moduleRuntimeScope(ModuleUtil.findModuleForPsiElement(element), false));

            projectFiles.putIfAbsent(projectFilePath, containingFiles);
        }

        return new AgentDeviceReference(element, statementText, projectFiles.get(projectFilePath));
    }

    public static PsiReference getReference(SquirrelId element) {
        boolean inTestSourceContent = ProjectRootManager.getInstance(element.getProject()).getFileIndex().isInTestSourceContent(element.getContainingFile().getVirtualFile());
        String projectFilePath = Objects.requireNonNull(element.getProject().getProjectFilePath()) + inTestSourceContent;

        if (!projectFiles.containsKey(projectFilePath)) {
            Collection<VirtualFile> containingFiles = FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, SquirrelFileType.INSTANCE,
                    GlobalSearchScope.moduleRuntimeScope(ModuleUtil.findModuleForPsiElement(element), inTestSourceContent));

            projectFiles.putIfAbsent(projectFilePath, containingFiles);
        }

        if (!lookups.containsKey(element)) {
            lookups.putIfAbsent(element, new SquirrelFunctionDeclarationPsiReferenceBase(element, projectFiles.get(projectFilePath)));
        }
        return lookups.get(element);
    }

    public static class SquirrelFilePsiReferenceBase extends PsiPolyVariantReferenceBase<PsiElement> {

        private final PsiFile reference;

        SquirrelFilePsiReferenceBase(@NotNull PsiElement psiElement, PsiFile reference) {
            super(psiElement);
            this.reference = reference;
        }

        @NotNull
        @Override
        public Object[] getVariants() {
            return new Object[0];
        }

        @NotNull
        @Override
        public ResolveResult[] multiResolve(boolean b) {
            return new ResolveResult[]{new PsiElementResolveResult(reference)};
        }
    }

    public static class SquirrelFunctionDeclarationPsiReferenceBase extends PsiPolyVariantReferenceBase<PsiElement> {

        private Collection<VirtualFile> allFilesInProject;
        private AtomicReference<ResolveResult[]> cached = new AtomicReference<>();

        SquirrelFunctionDeclarationPsiReferenceBase(PsiElement element, Collection<VirtualFile> allFiles) {
            super(element);
            this.allFilesInProject = allFiles;
        }

        @NotNull
        @Override
        public Object[] getVariants() {
            return new Object[0];
        }

        @NotNull
        @Override
        public ResolveResult[] multiResolve(boolean b) {
            ResolveResult[] result = cached.get();
            if (result == null) {
                result = lookup();
                if (!cached.compareAndSet(null, result)) {
                    return cached.get();
                }
            }
            return result;
        }

        @NotNull
        private ResolveResult[] lookup() {
            List<ResolveResult> result = new ArrayList<>();

            paramSearch(result);
            if (result.isEmpty())
                searchInFile(result, myElement.getContainingFile().getVirtualFile());

            if (result.isEmpty())
                for (VirtualFile vf : allFilesInProject) {
                    searchInFile(result, vf);
                }

            return result.toArray(new ResolveResult[0]);
        }

        private void paramSearch(List<ResolveResult> result) {
            String id = myElement.getText();
            Collection<SquirrelParameter> parameterDeclarations = PsiTreeUtil.findChildrenOfType(myElement.getContainingFile(), SquirrelParameter.class);
            for (SquirrelParameter param : parameterDeclarations) {
                SquirrelId id1 = param.getId();

                if (id.equals(id1.getText()) && PsiTreeUtil.isAncestor(param.getParent().getParent().getParent(), myElement, false)) {
                    result.add(new PsiElementResolveResult(param));
                }
            }
        }

        private void searchInFile(List<ResolveResult> result, VirtualFile vf) {
            try {
                SquirrelFile squirrelFile = (SquirrelFile) PsiManager.getInstance(myElement.getProject()).findFile(vf);
                if (squirrelFile != null) {
                    try {
                        String id = myElement.getText();

                        if (result.isEmpty()) {
                            Collection<SquirrelVarItem> variables = PsiTreeUtil.findChildrenOfType(squirrelFile, SquirrelVarItem.class);
                            for (SquirrelVarItem methodDeclaration : variables) {
                                PsiElement variable = methodDeclaration.getId().getIdentifier();
                                if (id.equals(variable.getText())) {
                                    result.add(new PsiElementResolveResult(variable));
                                }
                            }
                        }

                        if (result.isEmpty()) {
                            Collection<SquirrelFunctionDeclaration> functionDeclarations = PsiTreeUtil.findChildrenOfType(squirrelFile, SquirrelFunctionDeclaration.class);
                            for (SquirrelFunctionDeclaration functionDeclaration : functionDeclarations) {
                                SquirrelFunctionName functionName1 = functionDeclaration.getFunctionName();
                                if (functionName1 != null) {
                                    if (id.equals(functionName1.getText())) {
                                        result.add(new PsiElementResolveResult(functionName1));
                                    }
                                }
                            }
                        }

                        if (result.isEmpty()) {
                            Collection<SquirrelMethodDeclaration> methodDeclarations = PsiTreeUtil.findChildrenOfType(squirrelFile, SquirrelMethodDeclaration.class);
                            for (SquirrelMethodDeclaration methodDeclaration : methodDeclarations) {
                                SquirrelFunctionName methodName1 = methodDeclaration.getFunctionName();
                                if (id.equals(methodName1.getText())) {
                                    result.add(new PsiElementResolveResult(methodName1));
                                }
                            }
                        }

                        if (result.isEmpty()) {
                            Collection<SquirrelConstDeclaration> className = PsiTreeUtil.findChildrenOfType(squirrelFile, SquirrelConstDeclaration.class);
                            for (SquirrelConstDeclaration constDeclaration : className) {
                                SquirrelId idList = constDeclaration.getId();
                                if (idList != null && id.equals(idList.getText())) {
                                    result.add(new PsiElementResolveResult(idList));
                                }
                            }
                        }
                        if (result.isEmpty()) {
                            Collection<SquirrelEnumItem> className = PsiTreeUtil.findChildrenOfType(squirrelFile, SquirrelEnumItem.class);
                            for (SquirrelEnumItem enumDeclaration : className) {
                                SquirrelId idList = enumDeclaration.getId();
                                if (id.equals(idList.getText())) {
                                    result.add(new PsiElementResolveResult(idList));
                                }
                            }
                        }
                        if (result.isEmpty()) {
                            Collection<SquirrelClassMember> variables = PsiTreeUtil.findChildrenOfType(squirrelFile, SquirrelClassMember.class);
                            for (SquirrelClassMember member : variables) {
                                PsiElement variable = member.getKey();
                                if (variable != null) {
                                    if (id.equals(variable.getText())) {
                                        result.add(new PsiElementResolveResult(variable));
                                    }
                                }
                            }
                        }

                        if (result.isEmpty()) {
                            Collection<SquirrelClassDeclaration> className = PsiTreeUtil.findChildrenOfType(squirrelFile, SquirrelClassDeclaration.class);
                            for (SquirrelClassDeclaration classDeclaration : className) {
                                SquirrelClassName idList = classDeclaration.getClassNameList().get(0);
                                if (id.equals(idList.getText())) {
                                    result.add(new PsiElementResolveResult(idList));
                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
    }

    private static class AgentDeviceReference extends PsiPolyVariantReferenceBase<PsiElement> {

        private final String statementText;
        private Collection<VirtualFile> virtualFiles;

        AgentDeviceReference(SquirrelStringLiteral element, String statementText, Collection<VirtualFile> virtualFiles) {
            super(element);
            this.statementText = statementText;
            this.virtualFiles = virtualFiles;
        }

        @NotNull
        @Override
        public ResolveResult[] multiResolve(boolean b) {
            for (VirtualFile vf : virtualFiles) {
                SquirrelFile squirrelFile = (SquirrelFile) PsiManager.getInstance(myElement.getProject()).findFile(vf);
                Collection<SquirrelExpressionStatement> childrenOfType = PsiTreeUtil.findChildrenOfType(squirrelFile, SquirrelExpressionStatement.class);

                for (SquirrelExpressionStatement squirrelExpressionStatement : childrenOfType) {
                    String prefix = (statementText.startsWith("agent") ? "device" : "agent") + "." + (statementText.contains("send") ? "on" : "send") + "(" + myElement.getText();
                    if (squirrelExpressionStatement.getText().startsWith(prefix)) {
                        return new ResolveResult[]{
                                new PsiElementResolveResult(squirrelExpressionStatement)};
                    }
                }

            }
            return new PsiElementResolveResult[0];
        }
    }
}