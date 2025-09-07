package org.foameraserblue.shop.domain.category.controller.dto

import org.foameraserblue.shop.domain.category.domain.CategoryTree

data class CategoryTreeResponse(
    val data: CategoryData,
) {
    companion object {
        fun toDto(categoryTrees: List<CategoryTree>): CategoryTreeResponse {
            val list = categoryTrees.map { mapTree(it) }

            return CategoryTreeResponse(
                data = CategoryData(list = list)
            )
        }

        fun toDto(categoryTree: CategoryTree): CategoryTreeResponse {
            val list = listOf(mapTree(categoryTree))

            return CategoryTreeResponse(
                data = CategoryData(list = list)
            )
        }

        private fun mapTree(tree: CategoryTree): CategoryData.CategoryItemResponse {
            val children = tree.children.map { child -> mapTree(child) }

            return CategoryData.CategoryItemResponse(
                code = tree.category.code,
                title = tree.category.title,
                hasChildren = children.isNotEmpty(),
                parentCode = tree.category.parentCode ?: "",
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
