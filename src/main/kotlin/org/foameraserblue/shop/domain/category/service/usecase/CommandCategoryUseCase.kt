package org.foameraserblue.shop.domain.category.service.usecase

import org.foameraserblue.shop.domain.category.domain.Category

interface CommandCategoryUseCase {
    fun createCategory(title: String, parentCategoryId: Long?): Category

    fun updateCategory(id: Long, title: String?): Category

    // 위치변경 : 뎁스 이동, 같은 뎁스내의 순서이동
    fun moveCategoryLocation(
        id: Long,
        fromParentCategoryId: Long?,
        toParentCategoryId: Long?,
        fromSortOrderOfSameDepth: Int?,
        toSortOrderOfSameDepth: Int?,
    ): Category

    fun deleteCategory(id: Long)
}