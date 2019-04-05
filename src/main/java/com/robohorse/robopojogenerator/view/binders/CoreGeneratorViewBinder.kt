package com.robohorse.robopojogenerator.view.binders

import com.intellij.openapi.ui.DialogBuilder
import com.robohorse.robopojogenerator.listeners.*
import com.robohorse.robopojogenerator.view.ui.CoreGeneratorVew

import javax.inject.Inject

/**
 * Created by vadim on 24.09.16.
 */
class CoreGeneratorViewBinder @Inject
constructor() {

    fun bindView(builder: DialogBuilder, eventListener: GuiFormEventListener) {
        val generatorVew = CoreGeneratorVew()

        val actionListener = CoreGenerateActionListener(generatorVew, eventListener)
        generatorVew.generateButton.addActionListener(actionListener)
        generatorVew.domainPathButton.addActionListener(object : MouseListenerClicked(generatorVew.domainPath) {})
        generatorVew.cachePathButton.addActionListener(object : MouseListenerClicked(generatorVew.cachePath) {})
        generatorVew.dataPathButton.addActionListener(object : MouseListenerClicked(generatorVew.dataPath) {})
        generatorVew.roguePathButton.addActionListener(object : MouseListenerClicked(generatorVew.roguePath) {})

        builder.setCenterPanel(generatorVew.rootView)
        builder.setTitle("Core Generator")
        builder.removeAllActions()
        builder.show()

    }
}
