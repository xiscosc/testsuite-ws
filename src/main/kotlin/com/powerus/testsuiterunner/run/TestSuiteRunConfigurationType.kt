package com.powerus.testsuiterunner.run

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.icons.AllIcons
import javax.swing.Icon

class TestSuiteRunConfigurationType : ConfigurationType {
    override fun getDisplayName(): String = "TestSuite"

    override fun getConfigurationTypeDescription(): String = 
        "Run configuration for TestSuite wrapped tests"

    override fun getIcon(): Icon = AllIcons.RunConfigurations.TestState.Run_run

    override fun getId(): String = "TestSuiteRunConfiguration"

    override fun getConfigurationFactories(): Array<ConfigurationFactory> {
        return arrayOf(TestSuiteConfigurationFactory(this))
    }

    companion object {
        fun getInstance(): TestSuiteRunConfigurationType {
            return ConfigurationType.CONFIGURATION_TYPE_EP.findExtension(TestSuiteRunConfigurationType::class.java)!!
        }
    }
}
