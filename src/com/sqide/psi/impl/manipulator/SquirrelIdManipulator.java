package com.sqide.psi.impl.manipulator;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import com.sqide.psi.SquirrelId;
import com.sqide.psi.impl.SquirrelExpressionImpl;
import com.sqide.psi.impl.SquirrelIdImpl;
import org.jetbrains.annotations.NotNull;

public class SquirrelIdManipulator extends AbstractElementManipulator<SquirrelIdImpl> {
  @Override
  public SquirrelIdImpl handleContentChange(@NotNull SquirrelIdImpl literal, @NotNull TextRange range, String newContent)
    throws IncorrectOperationException {
    throw new UnsupportedOperationException();
  }

  @NotNull
  @Override
  public TextRange getRangeInElement(@NotNull SquirrelIdImpl element) {
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

  private static boolean isQuote(char ch) {
    return ch == '"' || ch == '\'' || ch == '`';
  }
}
