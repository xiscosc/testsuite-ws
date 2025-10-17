package com.powerus.testsuiterunner

import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testIntegration.TestFinder
import com.intellij.testIntegration.TestFinderHelper
import com.intellij.lang.javascript.psi.*

class TestSuiteTestFinder : TestFinder {
    override fun findClassesForTest(element: PsiElement): Collection<PsiElement> {
        return emptyList()
    }

    override fun findTestsForClass(element: PsiElement): Collection<PsiElement> {
        val file = element.containingFile
        if (file !is JSFile) return emptyList()
        
        return findTestSuiteElements(file)
    }

    override fun isTest(element: PsiElement): Boolean {
        return isTestSuiteElement(element) || isTestElement(element)
    }

    override fun findSourceElement(element: PsiElement): PsiElement? {
        return element
    }

    companion object {
        fun isTestSuiteElement(element: PsiElement): Boolean {
            if (element !is JSCallExpression) return false
            
            val methodExpression = element.methodExpression
            if (methodExpression is JSReferenceExpression) {
                // Check if this is a .build() call at the end of the chain
                if (methodExpression.referenceName == "build") {
                    // Walk back through the chain to find TestSuite.new()
                    return isTestSuiteChain(methodExpression.qualifier)
                }
            }
            return false
        }
        
        private fun isTestSuiteChain(element: PsiElement?): Boolean {
            if (element == null) return false
            
            when (element) {
                is JSCallExpression -> {
                    val methodExpr = element.methodExpression
                    if (methodExpr is JSReferenceExpression) {
                        // Check if this is TestSuite.new()
                        if (methodExpr.referenceName == "new") {
                            val qualifier = methodExpr.qualifier
                            if (qualifier is JSReferenceExpression && qualifier.referenceName == "TestSuite") {
                                return true
                            }
                        }
                        
                        // Continue walking back through the chain
                        val qualifier = methodExpr.qualifier
                        if (qualifier != null) {
                            return isTestSuiteChain(qualifier)
                        }
                    }
                    return false
                }
                else -> return false
            }
        }

        fun isTestElement(element: PsiElement): Boolean {
            if (element !is JSCallExpression) return false
            
            val methodExpression = element.methodExpression
            if (methodExpression is JSReferenceExpression) {
                val name = methodExpression.referenceName
                return name == "it" || name == "test" || name == "describe"
            }
            return false
        }

        fun findTestSuiteElements(file: JSFile): List<PsiElement> {
            val result = mutableListOf<PsiElement>()
            PsiTreeUtil.findChildrenOfType(file, JSCallExpression::class.java).forEach { call ->
                if (isTestSuiteElement(call)) {
                    result.add(call)
                }
            }
            return result
        }

        fun findTestElements(element: PsiElement): List<PsiElement> {
            val result = mutableListOf<PsiElement>()
            PsiTreeUtil.findChildrenOfType(element, JSCallExpression::class.java).forEach { call ->
                if (isTestElement(call)) {
                    result.add(call)
                }
            }
            return result
        }

        fun getTestName(element: PsiElement): String? {
            if (element !is JSCallExpression) return null
            
            val arguments = element.arguments
            if (arguments.isNotEmpty()) {
                val firstArg = arguments[0]
                if (firstArg is JSLiteralExpression && firstArg.isQuotedLiteral) {
                    return firstArg.stringValue
                }
            }
            return null
        }
    }
}
