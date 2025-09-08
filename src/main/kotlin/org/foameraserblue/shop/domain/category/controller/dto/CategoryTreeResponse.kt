package org.foameraserblue.shop.domain.category.controller.dto

import org.foameraserblue.shop.domain.category.domain.Category

data class CategoryTreeResponse(
    val data: CategoryData,
) {
    companion object {
        fun toDto(categories: List<Category>): CategoryTreeResponse {
            val childrenByParent: Map<String, List<Category>> =
                categories.groupBy { it.parentCodeOrEmpty }

            val codeSet = categories.map { it.code }.toSet()
            val topNodes = categories
                .filter { it.parentCodeOrEmpty !in codeSet }
                .sortedBy { it.code }

            val list = topNodes.map { mapToItem(it, childrenByParent) }
            return CategoryTreeResponse(
                data = CategoryData(list = list)
            )
        }


        private fun mapToItem(
            category: Category,
            childrenByParent: Map<String, List<Category>>
        ): CategoryData.CategoryItemResponse {
            val children = childrenByParent[category.code]
                .orEmpty()
                .map { child -> mapToItem(child, childrenByParent) }

            return CategoryData.CategoryItemResponse(
                code = category.code,
                title = category.title,
                hasChildren = children.isNotEmpty(),
                parentCode = category.parentCodeOrEmpty,
                childrenList = children
            )
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
