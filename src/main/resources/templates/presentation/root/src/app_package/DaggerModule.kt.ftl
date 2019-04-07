package ${packageName};

import javax.inject.Inject
import com.app.dealfish.di.scopes.PerActivity
import dagger.Module
import dagger.Provides

@Module
class ${CLASS_NAME}Module {

    @PerActivity
    @Provides
    internal fun provide${CLASS_NAME}View(view: ${CLASS_NAME}Fragment): ${CLASS_NAME}Contract.View = view

    @PerActivity
    @Provides
    internal fun provide${CLASS_NAME}Presenter(view: ${CLASS_NAME}Contract.View): ${CLASS_NAME}Contract.Presenter {
        return ${CLASS_NAME}Presenter(view)
    }
}