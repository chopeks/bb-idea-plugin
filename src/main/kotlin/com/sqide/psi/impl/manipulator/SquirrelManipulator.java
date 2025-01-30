package com.sqide.psi.impl.manipulator;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

public abstract class SquirrelManipulator<T extends PsiElement> extends AbstractElementManipulator<T> {
    private static boolean isQuote(char ch) {
      return ch == '"' || ch == '\'' || ch == '`';
    }

    @Override
    public T handleContentChange(@NotNull T literal, @NotNull TextRange range, String newContent)
      throws IncorrectOperationException {
      throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public TextRange getRangeInElement(@NotNull T element) {
      if (element.getTextLength() == 0) {
        return TextRange.EMPTY_RANGE;
      }
      String s = element.getText();
      char quote = s.charAt(0);
      int startOffset = isQuote(quote) ? 1 : 0;
      int endOffset = s.length();
      if (s.length() > 1) {
        char lastChar = s.charAt(s.length() - 1);
        if (isQuote(quote) && lastChar == quote) {
          endOffset = s.length() - 1;
        }
        if (!isQuote(quote) && isQuote(lastChar)) {
          endOffset = s.length() - 1;
        }
      }
      return TextRange.create(startOffset, endOffset);
    }
}
