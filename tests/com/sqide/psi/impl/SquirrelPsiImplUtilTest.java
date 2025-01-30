package com.chopeks.psi.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase;
import com.chopeks.psi.SquirrelIncludeDeclaration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SquirrelPsiImplUtilTest extends LightPlatformCodeInsightFixture4TestCase {

    @Mock SquirrelIncludeDeclaration includeDeclaration;
    @Mock PsiElement psiElementFilename;

    @Override
    protected String getTestDataPath() {
        return Paths.get("").toAbsolutePath().toString() + "/testData/files";
    }

    @Before
    public void before() {
        myFixture.configureByFiles("nutty.nut");
        PsiElement psiElement = myFixture.getFile();

        when(psiElementFilename.getText()).thenReturn("\"unknown.nut\"");

        when(includeDeclaration.getString()).thenReturn(psiElementFilename);
        when(includeDeclaration.getProject()).thenReturn(psiElement.getProject());
    }

    @Test
    public void GIVEN_include_declaration_is_null_THEN_return_null_reference() {
        assertThat(SquirrelPsiImplUtil.getReference(includeDeclaration)).isNull();
    }

    @Test
    public void GIVEN_include_declaration_is_valid_WHEN_asking_project_AND_file_is_not_found_THEN_return_null() {
        when(psiElementFilename.getText()).thenReturn("\"unknown.nut\"");

        PsiReference psiReference = SquirrelPsiImplUtil.getReference(includeDeclaration);

        assertThat(psiReference).isNull();
    }

    @Test
    public void GIVEN_include_declaration_is_valid_WHEN_asking_project_AND_file_in_project_THEN_reference_is_not_null() {
        when(psiElementFilename.getText()).thenReturn("\"nutty.nut\"");

        PsiReference psiReference = SquirrelPsiImplUtil.getReference(includeDeclaration);

        assertThat(psiReference).isNotNull();
    }
}
