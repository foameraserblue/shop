package org.foameraserblue.shop.domain.category.controller.dto

import org.foameraserblue.shop.domain.category.domain.Category

data class CategoryTreeResponse(
    val data: CategoryData,
) {
    companion object {
        fun toDto(categories: List<Category>): CategoryTreeResponse {
            val childrenByParentId: Map<Long?, List<Category>> =
                categories
                    .sortedWith(compareBy { it.depth })
                    .groupBy { it.parentId }

            fun buildItem(category: Category): CategoryData.CategoryItemResponse {
                val children = childrenByParentId[category.id].orEmpty()
                val childItems = children.map { buildItem(it) }

                return CategoryData.CategoryItemResponse(
                    categoryCode = category.id.toString(),
                    categoryTitle = category.title,
                    hasSubCategory = childItems.isNotEmpty(),
                    parentCategoryCode = category.parentId?.toString() ?: "",
                    categoryList = childItems
                )
            }

            val items =
                childrenByParentId
                    .entries
                    .firstOrNull()
                    ?.value
                    ?.map { category -> buildItem(category) }
                    ?: emptyList()

            return CategoryTreeResponse(
                data = CategoryData(list = items)
            )
        }
    }

    data class CategoryData(
        val list: List<CategoryItemResponse>
    ) {
        data class CategoryItemResponse(
            val categoryCode: String,
            val categoryTitle: String,
            val hasSubCategory: Boolean,
            val parentCategoryCode: String,
            val categoryList: List<CategoryItemResponse>
        )
    }
}
