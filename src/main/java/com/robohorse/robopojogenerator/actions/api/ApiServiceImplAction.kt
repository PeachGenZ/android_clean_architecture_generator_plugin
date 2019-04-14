package com.robohorse.robopojogenerator.actions.api

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.robohorse.robopojogenerator.controllers.ClassNameTemplateActionController
import com.robohorse.robopojogenerator.injections.Injector
import com.robohorse.robopojogenerator.models.ClassNameTemplateModel
import javax.inject.Inject

class ApiServiceImplAction : AnAction() {

    @Inject
    lateinit var controller: ClassNameTemplateActionController

    init {
        Injector.initModules()
        Injector.getAppComponent().inject(this)
    }

    override fun actionPerformed(e: AnActionEvent) {
        val classNameTemplateModel = ClassNameTemplateModel(
                dialogTitle = "Api Service Impl",
                templateName = "ApiServiceImpl",
                fileNameSuffix = "ServiceImpl"
        )
        controller.onActionHandled(e, classNameTemplateModel)
    }
}
