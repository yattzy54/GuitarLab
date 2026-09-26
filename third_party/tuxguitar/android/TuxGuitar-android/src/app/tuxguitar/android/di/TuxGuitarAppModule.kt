package app.tuxguitar.android.di

import android.content.Context
import app.tuxguitar.action.TGActionManager
import app.tuxguitar.android.browser.TGBrowserManager
import app.tuxguitar.editor.TGEditorManager
import app.tuxguitar.util.TGContext
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TuxGuitarAppModule {

    @Provides
    @Singleton
    fun provideTGContext(@ApplicationContext context: Context): TGContext {
        return TGContext()
    }

    @Provides
    @Singleton
    fun provideTGActionManager(context: TGContext): TGActionManager {
        return TGActionManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideTGBrowserManager(context: TGContext): TGBrowserManager {
        return TGBrowserManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideTGEditorManager(context: TGContext): TGEditorManager {
        return TGEditorManager.getInstance(context)
    }
}
