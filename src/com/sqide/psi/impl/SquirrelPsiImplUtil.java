package com.sqide.psi.impl;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.FileBasedIndex;
import com.sqide.SquirrelFileType;
import com.sqide.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.SystemIndependent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SquirrelPsiImplUtil {

    public static PsiElement setName(SquirrelFunctionDeclaration element, String newName) {
//        ASTNode keyNode = element.getNode().findChildByType(SquirrelTokenTypes.KEY);
//        if (keyNode != null) {
//            SquirrelFunctionName funcName = new SquirrelFunctionNameImpl(element.getNode()).;
//            funcName.setName(newName);
//            ASTNode newKeyNode = funcName.getFirstChild().getNode();
//            element.getNode().replaceChild(keyNode, newKeyNode);
//        }
        return element;
    }

    public static PsiElement getNameIdentifier(SquirrelFunctionDeclaration element) {
        return element.getFunctionName();
    }

    private static ConcurrentHashMap<String, Collection<VirtualFile>> projectFiles = new ConcurrentHashMap<>();


    public static PsiReference getReference(SquirrelId element) {
        @SystemIndependent String projectFilePath = element.getProject().getProjectFilePath();
        if (!projectFiles.contains(projectFilePath)) {
            projectFiles.putIfAbsent(projectFilePath,
                    FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, SquirrelFileType.INSTANCE,
                            GlobalSearchScope.allScope(element.getProject())));
        }

        return new SquirrelFunctionDeclarationPsiReferenceBase(element, projectFiles.get(projectFilePath));
    }

    public static class SquirrelFunctionDeclarationPsiReferenceBase extends PsiPolyVariantReferenceBase<PsiElement> {

        private Collection<VirtualFile> virtualFiles;

        SquirrelFunctionDeclarationPsiReferenceBase(PsiElement element, Collection<VirtualFile> virtualFiles) {
            super(element);
            this.virtualFiles = virtualFiles;
        }

        @NotNull
        @Override
        public Object[] getVariants() {
            return new Object[0];
        }

        @NotNull
        @Override
        public ResolveResult[] multiResolve(boolean b) {
            List<ResolveResult> result = new ArrayList<>();
            String id = myElement.getText();



            for (VirtualFile vf : virtualFiles) {

                try {
                    SquirrelFile squirrelFile = (SquirrelFile) PsiManager.getInstance(myElement.getProject()).findFile(vf);
                    if (squirrelFile != null) {
                        try {
                            Collection<SquirrelFunctionDeclaration> functionDeclarations = PsiTreeUtil.findChildrenOfType(squirrelFile, SquirrelFunctionDeclaration.class);
                            for (SquirrelFunctionDeclaration functionDeclaration : functionDeclarations) {
                                SquirrelFunctionName functionName1 = functionDeclaration.getFunctionName();
                                if (functionName1 != null) {
                                    if (id.equals(functionName1.getText())) {
                                        result.add(new PsiElementResolveResult(functionName1));
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
                                Collection<SquirrelVarItem> variables = PsiTreeUtil.findChildrenOfType(squirrelFile, SquirrelVarItem.class);
                                for (SquirrelVarItem methodDeclaration : variables) {
                                    PsiElement variable = methodDeclaration.getId().getIdentifier();
                                    if (id.equals(variable.getText())) {
                                        result.add(new PsiElementResolveResult(variable));
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
                            if (result.isEmpty()) {
                                Collection<SquirrelConstDeclaration> className = PsiTreeUtil.findChildrenOfType(squirrelFile, SquirrelConstDeclaration.class);
                                for (SquirrelConstDeclaration classDeclaration : className) {
                                    SquirrelId idList = classDeclaration.getId();
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

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            }
            return result.toArray(new ResolveResult[0]);
        }
    }
}