package com.powerus.testsuiterunner.actions

import com.intellij.execution.RunManager
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.ExecutionUtil
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.powerus.testsuiterunner.TestSuiteTestFinder
import com.powerus.testsuiterunner.run.TestSuiteRunConfiguration
import com.powerus.testsuiterunner.run.TestSuiteRunConfigurationType

class RunTestSuiteAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val file = e.getData(CommonDataKeys.PSI_FILE) as? JSFile ?: return
        
        // Check if this file contains TestSuite tests
        val hasTestSuite = TestSuiteTestFinder.findTestSuiteElements(file).isNotEmpty()
        if (!hasTestSuite) return
        
        val runManager = RunManager.getInstance(project)
        val factory = TestSuiteRunConfigurationType.getInstance().configurationFactories[0]
        
        val settings = runManager.createConfiguration("TestSuite: ${file.name}", factory)
        val configuration = settings.configuration as TestSuiteRunConfiguration
        
        configuration.testFilePath = file.virtualFile.path
        configuration.workingDirectory = project.basePath
        
        runManager.addConfiguration(settings)
        runManager.selectedConfiguration = settings
        
        ExecutionUtil.runConfiguration(settings, DefaultRunExecutor.getRunExecutorInstance())
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        val file = e.getData(CommonDataKeys.PSI_FILE) as? JSFile
        
        val hasTestSuite = file?.let { 
            TestSuiteTestFinder.findTestSuiteElements(it).isNotEmpty() 
        } ?: false
        
        e.presentation.isEnabledAndVisible = project != null && hasTestSuite
    }
}
