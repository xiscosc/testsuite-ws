package com.powerus.testsuiterunner.run

import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.DefaultProgramRunner

class TestSuiteRunner : DefaultProgramRunner() {
    
    override fun getRunnerId(): String = "TestSuiteRunner"

    override fun canRun(executorId: String, profile: RunProfile): Boolean {
        return DefaultRunExecutor.EXECUTOR_ID == executorId && profile is TestSuiteRunConfiguration
    }
}
