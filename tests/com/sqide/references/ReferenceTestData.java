package com.sqide.references;

import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.sqide.psi.impl.SquirrelPsiImplUtil;

public class ReferenceTestData extends LightCodeInsightFixtureTestCase {

    @Override
    protected String getTestDataPath() {
        return "../squirrel-lang-idea-plugin/testData";
    }

    public void testReference() {
        myFixture.configureByFiles("parser/samples/ackermann.nut");

        PsiElement a = myFixture.getFile().findElementAt(myFixture.getCaretOffset())
                .getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling()
                .getNextSibling().getNextSibling().getNextSibling().getChildren()[0].getChildren()[1]
                .getFirstChild().getNextSibling().getFirstChild().getChildren()[2].getChildren()[0];

        assertEquals(1, a.getReferences().length);
    }
}
