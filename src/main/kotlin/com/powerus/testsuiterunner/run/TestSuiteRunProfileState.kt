package com.powerus.testsuiterunner.run

import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.KillableColoredProcessHandler
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.project.Project
import java.io.File

class TestSuiteRunProfileState(
    private val environment: ExecutionEnvironment,
    private val configuration: TestSuiteRunConfiguration
) : CommandLineState(environment) {

    override fun startProcess(): ProcessHandler {
        try {
            val commandLine = createCommandLine(environment.project)
            val processHandler = KillableColoredProcessHandler(commandLine)
            ProcessTerminatedListener.attach(processHandler)
            return processHandler
        } catch (e: Exception) {
            throw RuntimeException("Failed to start TestSuite tests: ${e.message}", e)
        }
    }

    private fun createCommandLine(project: Project): GeneralCommandLine {
        val testFile = configuration.testFilePath ?: throw IllegalStateException("Test file path is not set")

        val commandLine = GeneralCommandLine()
        // Set working directory to PROJECT ROOT, not module directory
        commandLine.withWorkDirectory(project.basePath)

        // Try to locate a local Jest script
        val jestScript = findJestExecutable(project.basePath)

        if (jestScript != null) {
            commandLine.exePath = "node"
            commandLine.addParameter(jestScript)
        } else {
            // Fallback: use npx to run jest
            commandLine.exePath = "npx"
            commandLine.addParameter("jest")
        }

        // Add test file first (Jest expects file before options)
        commandLine.addParameter(testFile)

        // Add jest config - only if found or explicitly set
        val jestConfig = configuration.jestConfigPath ?: findJestConfig(testFile)
        if (!jestConfig.isNullOrBlank()) {
            commandLine.addParameter("-c")
            commandLine.addParameter(jestConfig)
        }

        // Add test name pattern if specified
        val testName = configuration.testName
        if (!testName.isNullOrBlank()) {
            commandLine.addParameter("-t")
            commandLine.addParameter(testName)
        }
        
        // Add --colors to get proper colored output
        commandLine.addParameter("--colors")
        
        // Add --verbose to show individual test results
        commandLine.addParameter("--verbose")
        
        // Add --expand to show full diffs for failed tests
        commandLine.addParameter("--expand")
        
        // Add --no-coverage to disable coverage by default for faster runs
        commandLine.addParameter("--no-coverage")

        return commandLine
    }

    private fun findJestExecutable(projectBase: String?): String? {
        if (projectBase == null) return null
        
        val candidates = listOf(
            File(projectBase, "node_modules/.bin/jest"),
            File(projectBase, "node_modules/jest/bin/jest.js")
        )

        for (candidate in candidates) {
            if (candidate.exists()) return candidate.absolutePath
        }

        return null
    }

    private fun findJestConfig(testFilePath: String): String? {
        val testFile = File(testFilePath)
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
                val configFile = File(currentDir, configName)
                if (configFile.exists()) {
                    return configFile.absolutePath
                }
            }
            
            // Also check for jest config in package.json
            val packageJson = File(currentDir, "package.json")
            if (packageJson.exists()) {
                // If package.json exists and has jest config, we found the right directory
                // Return the directory path and let jest find the config
                val configFile = File(currentDir, "jest.config.ts")
                if (configFile.exists()) {
                    return configFile.absolutePath
                }
            }
            
            currentDir = currentDir.parentFile
        }
        
        return null
    }
}
