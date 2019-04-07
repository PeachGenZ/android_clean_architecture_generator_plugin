package com.robohorse.robopojogenerator.view.binders

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.DialogBuilder
import com.robohorse.robopojogenerator.components.ProjectConfigurationComponent
import com.robohorse.robopojogenerator.listeners.*
import com.robohorse.robopojogenerator.models.GenerationModel
import com.robohorse.robopojogenerator.view.ui.CorePOJOGeneratorVew

import javax.inject.Inject

/**
 * Created by vadim on 24.09.16.
 */
class CorePOJOGeneratorViewBinder @Inject constructor() {

    fun bindView(builder: DialogBuilder, event: AnActionEvent, generationModel: GenerationModel, eventListener: GuiFormEventListener) {
        val generatorVew = CorePOJOGeneratorVew()
        val basePath = event.project?.basePath

        val actionListener = CoreGenerateActionListener(generatorVew, event, generationModel, eventListener)
        generatorVew.generateButton.addActionListener(actionListener)

        event.project?.let {
            val component = ProjectConfigurationComponent.getInstance(it)
        }

        builder.setCenterPanel(generatorVew.rootView)
        builder.setTitle("Core POJO & Mapper Generator")
        builder.removeAllActions()
        builder.show()
    }
}
