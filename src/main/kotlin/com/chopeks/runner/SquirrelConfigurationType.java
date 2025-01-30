package com.chopeks.runner;

import com.chopeks.SquirrelIcons;
import com.intellij.compiler.options.CompileStepBeforeRun;
import com.intellij.execution.BeforeRunTask;
import com.intellij.execution.RunManagerEx;
import com.intellij.execution.configuration.ConfigurationFactoryEx;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationModule;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

/**
 * Squirrel run configuration type.
 *
 * @author jansorg
 */
public class SquirrelConfigurationType extends ConfigurationTypeBase {
    public SquirrelConfigurationType() {
        super("SquirrelConfigurationType", "Squirrel", "Squirrel run configuration", SquirrelIcons.NUT_FILE);

        addFactory(new SquirrelConfigurationFactory(this));
    }

    @NotNull
    public static SquirrelConfigurationType getInstance() {
        return ConfigurationTypeUtil.findConfigurationType(SquirrelConfigurationType.class);
    }

    private static class SquirrelConfigurationFactory extends ConfigurationFactoryEx {
        public SquirrelConfigurationFactory(SquirrelConfigurationType configurationType) {
            super(configurationType);
        }

        @Override
        public @NotNull @NonNls String getId() {
            return "SquirrelConfigurationType";
        }

        @Override
        public void onNewConfigurationCreated(@NotNull RunConfiguration configuration) {
            //the last param has to be false because we do not want a fallback to the template (we're creating it
            // right now) (avoiding a SOE)
            RunManagerEx.getInstanceEx(configuration.getProject()).setBeforeRunTasks(configuration, Collections.emptyList());
        }

        @Override
        public @NotNull RunConfiguration createTemplateConfiguration(@NotNull Project project) {
            return new SquirrelRunConfiguration(new RunConfigurationModule(project), this, "");
        }

        @Override
        public void configureBeforeRunTaskDefaults(Key<? extends BeforeRunTask> providerID, BeforeRunTask task) {
            super.configureBeforeRunTaskDefaults(providerID, task);
            if (providerID == CompileStepBeforeRun.ID) {
                task.setEnabled(false);
            }
        }
    }
}
