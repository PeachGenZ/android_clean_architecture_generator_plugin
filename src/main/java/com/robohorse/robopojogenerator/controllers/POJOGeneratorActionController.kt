package com.robohorse.robopojogenerator.controllers

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.DialogBuilder
import com.robohorse.robopojogenerator.delegates.*
import com.robohorse.robopojogenerator.errors.RoboPluginException
import com.robohorse.robopojogenerator.listeners.GuiFormEventListener
import com.robohorse.robopojogenerator.models.GenerationModel
import com.robohorse.robopojogenerator.view.binders.CoreGeneratorViewBinder
import com.robohorse.robopojogenerator.view.binders.POJOGeneratorViewBinder
import javax.inject.Inject

open class POJOGeneratorActionController @Inject constructor() {

    @Inject
    lateinit var environmentDelegate: EnvironmentDelegate

    @Inject
    lateinit var messageDelegate: MessageDelegate

    @Inject
    lateinit var coreGeneratorViewBinder: POJOGeneratorViewBinder

    @Inject
    lateinit var generationDelegate: POJOGenerationDelegate

    fun onActionHandled(event: AnActionEvent, generationModel: GenerationModel) {
        try {
            proceed(event, generationModel)
        } catch (exception: RoboPluginException) {
            messageDelegate.onPluginExceptionHandled(exception)
        }
    }

    private fun proceed(event: AnActionEvent, generationModel: GenerationModel) {
        val projectModel = environmentDelegate.obtainProjectModel(event)
        val dialogBuilder = DialogBuilder()
        val window = dialogBuilder.window

        coreGeneratorViewBinder.bindView(dialogBuilder, event, generationModel, GuiFormEventListener { generationModel ->
            window.dispose()
            generationDelegate.runGenerationTask(generationModel, projectModel)
        })
    }
}