package com.powerus.testsuiterunner.run

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.LazyRunConfigurationProducer
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.lang.javascript.psi.JSCallExpression
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.powerus.testsuiterunner.TestSuiteTestFinder

class TestSuiteRunConfigurationProducer : LazyRunConfigurationProducer<TestSuiteRunConfiguration>() {

    override fun getConfigurationFactory(): ConfigurationFactory {
        return TestSuiteRunConfigurationType.getInstance().configurationFactories[0]
    }

    override fun isConfigurationFromContext(
        configuration: TestSuiteRunConfiguration,
        context: ConfigurationContext
    ): Boolean {
        val location = context.location ?: return false
        val element = location.psiElement
        val file = element.containingFile
        
        if (file !is JSFile) return false
        
        val testFile = file.virtualFile.path
        if (configuration.testFilePath != testFile) return false
        
        // Check if we're running a specific test
        val testCall = findTestCall(element)
        if (testCall != null) {
            val testName = TestSuiteTestFinder.getTestName(testCall)
            return configuration.testName == testName
        }
        
        // Running the whole file
        return configuration.testName == null || configuration.testName!!.isEmpty()
    }

    override fun setupConfigurationFromContext(
        configuration: TestSuiteRunConfiguration,
        context: ConfigurationContext,
        sourceElement: Ref<PsiElement>
    ): Boolean {
        val location = context.location ?: return false
        val element = location.psiElement
        val file = element.containingFile
        
        if (file !is JSFile) return false
        
        // Check if this file contains TestSuite tests
        val hasTestSuite = TestSuiteTestFinder.findTestSuiteElements(file).isNotEmpty()
        if (!hasTestSuite) return false
        
        val project = context.project
        configuration.testFilePath = file.virtualFile.path
        
        // Auto-detect Jest config
        val jestConfig = findJestConfigForFile(file.virtualFile.path)
        configuration.jestConfigPath = jestConfig
        
        // Set working directory to the jest config's directory (where node_modules should be)
        configuration.workingDirectory = if (jestConfig != null) {
            java.io.File(jestConfig).parent
        } else {
            project.basePath
        }
        
        // Check if we're running a specific test
        val testCall = findTestCall(element)
        if (testCall != null) {
            val testName = TestSuiteTestFinder.getTestName(testCall)
            configuration.testName = testName
            configuration.name = "TestSuite: $testName"
        } else {
            configuration.name = "TestSuite: ${file.name}"
        }
        
        return true
    }
    
    private fun findJestConfigForFile(testFilePath: String): String? {
        val testFile = java.io.File(testFilePath)
        var currentDir = testFile.parentFile
        
        // Walk up the directory tree looking for jest config files
        while (currentDir != null) {
            val configNames = listOf(
                "jest.config.ts",
                "jest.config.js",
                "jest.config.mjs",
                "jest.config.cjs",
                "jest.config.json"
            )
            
            for (configName in configNames) {
                val configFile = java.io.File(currentDir, configName)
                if (configFile.exists()) {
                    return configFile.absolutePath
                }
            }
            
            currentDir = currentDir.parentFile
        }
        
        return null
    }

    private fun findTestCall(element: PsiElement): JSCallExpression? {
        var current: PsiElement? = element
        
        // Walk up the tree to find the nearest it/test/describe call
        while (current != null) {
            if (current is JSCallExpression && TestSuiteTestFinder.isTestElement(current)) {
                return current
            }
            current = current.parent
        }
        
        // If not found by walking up, try to find the nearest one
        val file = element.containingFile
        val offset = element.textOffset
        
        val allTests = PsiTreeUtil.findChildrenOfType(file, JSCallExpression::class.java)
            .filter { TestSuiteTestFinder.isTestElement(it) }
        
        // Find the test that contains this offset
        return allTests.firstOrNull { test ->
            val range = test.textRange
            offset >= range.startOffset && offset <= range.endOffset
        }
    }
}
