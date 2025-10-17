package com.powerus.testsuiterunner.run

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

class TestSuiteSettingsEditor : SettingsEditor<TestSuiteRunConfiguration>() {
    private val testFilePathField = TextFieldWithBrowseButton()
    private val testNameField = JBTextField()
    private val workingDirectoryField = TextFieldWithBrowseButton()
    private val jestConfigPathField = TextFieldWithBrowseButton()
    
    private val panel: JPanel

    init {
        testFilePathField.addBrowseFolderListener(
            "Select Test File",
            null,
            null,
            FileChooserDescriptorFactory.createSingleFileDescriptor()
        )

        workingDirectoryField.addBrowseFolderListener(
            "Select Working Directory",
            null,
            null,
            FileChooserDescriptorFactory.createSingleFolderDescriptor()
        )

        jestConfigPathField.addBrowseFolderListener(
            "Select Jest Config",
            null,
            null,
            FileChooserDescriptorFactory.createSingleFileDescriptor()
        )

        panel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Test file:"), testFilePathField, 1, false)
            .addLabeledComponent(JBLabel("Test name (optional):"), testNameField, 1, false)
            .addLabeledComponent(JBLabel("Working directory:"), workingDirectoryField, 1, false)
            .addLabeledComponent(JBLabel("Jest config (optional):"), jestConfigPathField, 1, false)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    override fun resetEditorFrom(config: TestSuiteRunConfiguration) {
        testFilePathField.text = config.testFilePath ?: ""
        testNameField.text = config.testName ?: ""
        workingDirectoryField.text = config.workingDirectory ?: ""
        jestConfigPathField.text = config.jestConfigPath ?: ""
    }

    override fun applyEditorTo(config: TestSuiteRunConfiguration) {
        config.testFilePath = testFilePathField.text.takeIf { it.isNotBlank() }
        config.testName = testNameField.text.takeIf { it.isNotBlank() }
        config.workingDirectory = workingDirectoryField.text.takeIf { it.isNotBlank() }
        config.jestConfigPath = jestConfigPathField.text.takeIf { it.isNotBlank() }
    }

    override fun createEditor(): JComponent = panel
}
