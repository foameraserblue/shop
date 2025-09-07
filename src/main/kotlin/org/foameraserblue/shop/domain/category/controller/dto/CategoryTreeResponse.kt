package org.foameraserblue.shop.domain.category.controller.dto

import org.foameraserblue.shop.domain.category.domain.CategoryTree

data class CategoryTreeResponse(
    val data: CategoryData,
) {
    companion object {
        fun toDto(categoryTrees: List<CategoryTree>): CategoryTreeResponse {

        }
    }

    data class CategoryData(
        val list: List<CategoryItemResponse>
    ) {
        data class CategoryItemResponse(
            val code: String,
            val title: String,
            val hasChildren: Boolean,
            val parentCode: String,
            val childrenList: List<CategoryItemResponse>
        )
    }
}
