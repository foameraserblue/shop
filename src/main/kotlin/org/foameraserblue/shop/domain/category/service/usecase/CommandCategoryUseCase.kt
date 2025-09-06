package org.foameraserblue.shop.domain.category.service.usecase

import org.foameraserblue.shop.domain.category.domain.Category

interface CommandCategoryUseCase {
    fun createCategory(title: String, parentId: Long?): Category

    fun updateCategory(id: Long, title: String): Category

    // 위치변경 : 뎁스 이동, 같은 뎁스내의 순서이동, vo 만들까,,
    fun moveCategoryLocation(
        id: Long,
        fromParentId: Long?,
        toParentId: Long?,
        fromSiblingOrder: Int?,
        toSiblingOrder: Int?,
    ): Category

    fun deleteCategory(id: Long)
}