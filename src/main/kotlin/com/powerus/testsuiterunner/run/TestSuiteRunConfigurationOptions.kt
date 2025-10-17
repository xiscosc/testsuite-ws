package com.powerus.testsuiterunner.run

import com.intellij.execution.configurations.RunConfigurationOptions
import com.intellij.openapi.components.StoredProperty

class TestSuiteRunConfigurationOptions : RunConfigurationOptions() {
    private val testFilePath: StoredProperty<String?> = string("").provideDelegate(this, "testFilePath")
    private val testName: StoredProperty<String?> = string("").provideDelegate(this, "testName")
    private val workingDirectory: StoredProperty<String?> = string("").provideDelegate(this, "workingDirectory")
    private val jestConfigPath: StoredProperty<String?> = string("").provideDelegate(this, "jestConfigPath")

    fun getTestFilePath(): String? {
        val value = testFilePath.getValue(this)
        return if (value.isNullOrBlank()) null else value
    }
    fun setTestFilePath(value: String?) = testFilePath.setValue(this, value)

    fun getTestName(): String? {
        val value = testName.getValue(this)
        return if (value.isNullOrBlank()) null else value
    }
    fun setTestName(value: String?) = testName.setValue(this, value)

    fun getWorkingDirectory(): String? {
        val value = workingDirectory.getValue(this)
        return if (value.isNullOrBlank()) null else value
    }
    fun setWorkingDirectory(value: String?) = workingDirectory.setValue(this, value)

    fun getJestConfigPath(): String? {
        val value = jestConfigPath.getValue(this)
        return if (value.isNullOrBlank()) null else value
    }
    fun setJestConfigPath(value: String?) = jestConfigPath.setValue(this, value)
}
