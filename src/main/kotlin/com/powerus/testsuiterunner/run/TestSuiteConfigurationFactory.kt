package com.powerus.testsuiterunner.run

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.components.BaseState
import com.intellij.openapi.project.Project

class TestSuiteConfigurationFactory(type: ConfigurationType) : ConfigurationFactory(type) {
    override fun getId(): String = "TestSuite"

    override fun createTemplateConfiguration(project: Project): RunConfiguration {
        return TestSuiteRunConfiguration(project, this, "TestSuite")
    }

    override fun getOptionsClass(): Class<out BaseState> {
        return TestSuiteRunConfigurationOptions::class.java
    }
}
