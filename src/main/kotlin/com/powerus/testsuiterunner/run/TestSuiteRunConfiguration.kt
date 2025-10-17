package com.powerus.testsuiterunner.run

import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.InvalidDataException
import com.intellij.openapi.util.WriteExternalException
import org.jdom.Element

class TestSuiteRunConfiguration(
    project: Project,
    factory: ConfigurationFactory,
    name: String
) : RunConfigurationBase<TestSuiteRunConfigurationOptions>(project, factory, name) {

    override fun getOptions(): TestSuiteRunConfigurationOptions {
        return super.getOptions() as TestSuiteRunConfigurationOptions
    }

    var testFilePath: String?
        get() = options.getTestFilePath()
        set(value) = options.setTestFilePath(value)

    var testName: String?
        get() = options.getTestName()
        set(value) = options.setTestName(value)

    var workingDirectory: String?
        get() = options.getWorkingDirectory()
        set(value) = options.setWorkingDirectory(value)

    var jestConfigPath: String?
        get() = options.getJestConfigPath()
        set(value) = options.setJestConfigPath(value)

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> {
        return TestSuiteSettingsEditor()
    }

    override fun checkConfiguration() {
        super.checkConfiguration()
        if (testFilePath.isNullOrBlank()) {
            throw RuntimeConfigurationError("Test file path is not specified")
        }
    }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState {
        return TestSuiteRunProfileState(environment, this)
    }

    @Throws(InvalidDataException::class)
    override fun readExternal(element: Element) {
        super.readExternal(element)
    }

    @Throws(WriteExternalException::class)
    override fun writeExternal(element: Element) {
        super.writeExternal(element)
    }
}
