package com.chopeks.formatter.settings;

import com.chopeks.SquirrelLanguage;
import com.intellij.application.options.TabbedLanguageCodeStylePanel;
import com.intellij.psi.codeStyle.CodeStyleSettings;

public class SquirrelCodeStyleMainPanel extends TabbedLanguageCodeStylePanel {
  protected SquirrelCodeStyleMainPanel(CodeStyleSettings currentSettings, CodeStyleSettings settings) {
    super(SquirrelLanguage.INSTANCE, currentSettings, settings);
  }
}
