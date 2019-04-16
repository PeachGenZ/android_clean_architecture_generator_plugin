package com.robohorse.robopojogenerator.delegates

import com.intellij.psi.PsiManager
import com.robohorse.robopojogenerator.controllers.CoreGeneratorActionController
import com.robohorse.robopojogenerator.errors.custom.PathException
import com.robohorse.robopojogenerator.models.*
import java.io.File
import javax.inject.Inject

open class RemoteTestCreatorDelegate @Inject constructor() {

    @Inject
    lateinit var directoryCreatorDelegate: DirectoryCreatorDelegate

    @Inject
    lateinit var pOJOGenerationDelegate: POJOGenerationDelegate

    @Inject
    lateinit var mapperTestGeneratorDelegate: MapperTestGeneratorDelegate

    @Inject
    lateinit var factoryGenerationDelegate: FactoryGeneratorDelegate

    fun runGenerationTask(projectModel: ProjectModel, coreGeneratorModel: CoreGeneratorModel) {
        generateMapperTest(projectModel, coreGeneratorModel)
        generateFactory(projectModel, coreGeneratorModel)
    }

    private fun generateFactory(projectModel: ProjectModel, coreGeneratorModel: CoreGeneratorModel) {
        val path = coreGeneratorModel.remoteTestPath ?: throw PathException()
        val regenProjectModel = rejectProjectModel(projectModel, path)

        val generationModel = GenerationModel.Builder()
                .setContent(coreGeneratorModel.content)
                .setRootClassName(coreGeneratorModel.rootClassName)
                .build()

        val factoryGeneratorModel = FactoryGeneratorModel(
                fileNameSuffix = "Factory",
                templateName = "RemoteFactory",
                remote = true,
                cache = false,
                data = false,
                domain = false
        )

        factoryGenerationDelegate.runGenerationTask(generationModel, regenProjectModel, factoryGeneratorModel)
    }

    private fun generateMapperTest(projectModel: ProjectModel, coreGeneratorModel: CoreGeneratorModel) {
        val path = coreGeneratorModel.remoteTestPath + CoreGeneratorActionController.MAPPER_PATH
        val regenProjectModel = rejectProjectModel(projectModel, path)

        val generationModel = GenerationModel.Builder()
                .setContent(coreGeneratorModel.content)
                .setRootClassName(coreGeneratorModel.rootClassName)
                .build()

        val fromMapperTestGeneratorModel = MapperTestGeneratorModel(
                from = "model",
                to = "entity",
                fileNameSuffix = "EntityMapperTest",
                templateName = "FromRemoteMapperTest"
        )

        mapperTestGeneratorDelegate.runGenerationTask(generationModel, regenProjectModel, fromMapperTestGeneratorModel)

        val toMapperTestGeneratorModel = MapperTestGeneratorModel(
                from = "entity",
                to = "model",
                fileNameSuffix = "ModelMapperTest",
                templateName = "ToRemoteMapperTest"
        )

        mapperTestGeneratorDelegate.runGenerationTask(generationModel, regenProjectModel, toMapperTestGeneratorModel)
    }

    private fun rejectProjectModel(projectModel: ProjectModel, folderPath: String): ProjectModel {
        val projectDir = PsiManager.getInstance(projectModel.project)
                .findDirectory(projectModel.project.baseDir)
                ?: throw PathException()
        val path = projectModel.project.basePath + File.separator + folderPath
        val directory = directoryCreatorDelegate.createDirectory(projectModel, projectDir, path)
                ?: throw PathException()
        return ProjectModel.Builder()
                .setDirectory(directory)
                .setDirectoryPath(directory.virtualFile.path)
                .setPackageName(projectModel.packageName)
                .setProject(projectModel.project)
                .setVirtualFolder(projectModel.virtualFolder)
                .build()
    }
}