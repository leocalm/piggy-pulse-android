package com.piggypulse.android.core.repository

import com.piggypulse.android.core.model.CategoryDetail
import com.piggypulse.android.core.model.CategoryListItem
import com.piggypulse.android.core.model.CategoryResponse
import com.piggypulse.android.core.model.CreateCategoryRequest
import com.piggypulse.android.core.model.UpdateCategoryRequest
import com.piggypulse.android.core.network.ApiClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val apiClient: ApiClient,
) {
    suspend fun fetchAll(): Result<List<CategoryListItem>> {
        return apiClient.request { apiClient.service.getCategories(limit = 200) }
            .map { it.data }
    }

    suspend fun fetchDetail(id: String, periodId: String?): Result<CategoryDetail> {
        return apiClient.request { apiClient.service.getCategoryDetail(id, periodId) }
    }

    suspend fun create(request: CreateCategoryRequest): Result<CategoryResponse> {
        return apiClient.request { apiClient.service.createCategory(request) }
    }

    suspend fun update(id: String, request: UpdateCategoryRequest): Result<CategoryResponse> {
        return apiClient.request { apiClient.service.updateCategory(id, request) }
    }

    suspend fun delete(id: String): Result<Unit> {
        return apiClient.requestUnit { apiClient.service.deleteCategory(id) }
    }

    suspend fun archive(id: String): Result<CategoryResponse> {
        return apiClient.request { apiClient.service.archiveCategory(id) }
    }

    suspend fun unarchive(id: String): Result<CategoryResponse> {
        return apiClient.request { apiClient.service.unarchiveCategory(id) }
    }
}
