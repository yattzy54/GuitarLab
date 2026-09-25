package app.tuxguitar.android.di

import app.tuxguitar.android.data.editor.LegacyEditorRepository
import app.tuxguitar.android.domain.repository.EditorRepository
import app.tuxguitar.android.ui.editor.EditorHost
import app.tuxguitar.android.ui.editor.LegacyEditorHost
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class EditorModule {
    @Binds
    abstract fun bindEditorRepository(implementation: LegacyEditorRepository): EditorRepository

    @Binds
    abstract fun bindEditorHost(implementation: LegacyEditorHost): EditorHost
}
