package com.powerus.testsuiterunner.markers

import com.intellij.execution.lineMarker.ExecutorAction
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.lang.javascript.psi.JSCallExpression
import com.intellij.lang.javascript.psi.JSReferenceExpression
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.powerus.testsuiterunner.TestSuiteTestFinder

class TestSuiteLineMarkerProvider : RunLineMarkerContributor() {
    
    override fun getInfo(element: PsiElement): Info? {
        // Only add markers to identifiers
        if (!isIdentifier(element)) return null
        
        val parent = element.parent
        
        // Check if this is a TestSuite.new() call
        if (isTestSuiteNewCall(parent)) {
            return Info(
                AllIcons.RunConfigurations.TestState.Run_run,
                { "Run TestSuite" },
                *ExecutorAction.getActions(0)
            )
        }
        
        // Check if this is an it() or test() call inside a TestSuite
        if (isTestCall(parent) && isInsideTestSuite(element)) {
            val testName = if (parent is JSCallExpression) TestSuiteTestFinder.getTestName(parent) else null
            return Info(
                AllIcons.RunConfigurations.TestState.Run,
                { "Run '${testName ?: "test"}'" },
                *ExecutorAction.getActions(0)
            )
        }
        
        // Check if parent is a call expression that matches it.each pattern
        if (parent is JSCallExpression && TestSuiteTestFinder.isTestElement(parent) && isInsideTestSuite(element)) {
            val testName = TestSuiteTestFinder.getTestName(parent)
            return Info(
                AllIcons.RunConfigurations.TestState.Run,
                { "Run '${testName ?: "test"}'" },
                *ExecutorAction.getActions(0)
            )
        }
        
        // Check if parent's parent is an it.each()(...) call
        // This handles the case where we're on an identifier that's part of the outer call
        if (parent.parent is JSCallExpression) {
            val outerCall = parent.parent as JSCallExpression
            if (TestSuiteTestFinder.isTestElement(outerCall) && isInsideTestSuite(element)) {
                val testName = TestSuiteTestFinder.getTestName(outerCall)
                return Info(
                    AllIcons.RunConfigurations.TestState.Run,
                    { "Run '${testName ?: "test"}'" },
                    *ExecutorAction.getActions(0)
                )
            }
        }
        
        return null
    }

    private fun isIdentifier(element: PsiElement): Boolean {
        // Check if this is an identifier element (the actual text token)
        return element.node?.elementType?.toString()?.contains("IDENTIFIER") == true
    }

    private fun isTestSuiteNewCall(element: PsiElement): Boolean {
        if (element !is JSReferenceExpression) return false
        
        val parent = element.parent
        if (parent !is JSCallExpression) return false
        
        // Check if this is the "new" in TestSuite.new()
        if (element.referenceName == "new") {
            val qualifier = element.qualifier
            if (qualifier is JSReferenceExpression && qualifier.referenceName == "TestSuite") {
                return true
            }
        }
        
        return false
    }

    private fun isTestCall(element: PsiElement): Boolean {
        if (element !is JSReferenceExpression) return false
        
        val parent = element.parent
        if (parent !is JSCallExpression) return false
        
        val name = element.referenceName
        
        // Check for regular test calls
        if (name == "it" || name == "test" || name == "describe") {
            return true
        }
        
        // Check if this is "each" in an it.each([...])('test', ...) pattern
        // In this case, element is the "each" reference with "it" as qualifier
        if (name == "each") {
            val qualifier = element.qualifier
            if (qualifier is JSReferenceExpression && qualifier.referenceName == "it") {
                // Check if the parent (it.each([...])) is being called
                // The parent of parent should be the outer call expression
                val outerParent = parent.parent
                if (outerParent is JSCallExpression && outerParent.methodExpression == parent) {
                    return true
                }
            }
        }
        
        return false
    }

    private fun isInsideTestSuite(element: PsiElement): Boolean {
        // Walk up the tree to find if we're inside a TestSuite.new().build() call
        var current: PsiElement? = element
        while (current != null) {
            if (current is JSCallExpression) {
                val methodExpr = current.methodExpression
                if (methodExpr is JSReferenceExpression) {
                    // Check if this is a .build() call
                    if (methodExpr.referenceName == "build") {
                        val qualifier = methodExpr.qualifier
                        // Walk back through the chain to find TestSuite.new()
                        if (isPartOfTestSuiteChain(qualifier)) {
                            return true
                        }
                    }
                }
            }
            current = current.parent
        }
        return false
    }

    private fun isPartOfTestSuiteChain(element: PsiElement?): Boolean {
        if (element == null) return false
        
        when (element) {
            is JSCallExpression -> {
                val methodExpr = element.methodExpression
                if (methodExpr is JSReferenceExpression) {
                    val methodName = methodExpr.referenceName
                    
                    // Check if this is TestSuite.new()
                    if (methodName == "new") {
                        val qualifier = methodExpr.qualifier
                        if (qualifier is JSReferenceExpression && qualifier.referenceName == "TestSuite") {
                            return true
                        }
                    }
                    
                    // For any other method call in a chain, check the qualifier
                    // This handles with, withModule, withDatabase, as, and any custom chain methods
                    val qualifier = methodExpr.qualifier
                    if (qualifier != null) {
                        return isPartOfTestSuiteChain(qualifier)
                    }
                }
                return false
            }
            else -> return false
        }
    }
}
