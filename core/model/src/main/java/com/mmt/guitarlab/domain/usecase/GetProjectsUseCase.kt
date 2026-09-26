package com.mmt.guitarlab.domain.usecase

import com.mmt.guitarlab.domain.repository.TabProjectInfo
import com.mmt.guitarlab.domain.repository.TabRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProjectsUseCase @Inject constructor(
    private val tabRepository: TabRepository
) {
    operator fun invoke(): Flow<List<TabProjectInfo>> {
        return tabRepository.getAllProjects()
    }
}
