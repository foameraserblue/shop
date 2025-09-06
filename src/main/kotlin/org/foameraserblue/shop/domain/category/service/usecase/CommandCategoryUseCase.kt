package org.foameraserblue.shop.domain.category.service.usecase

import org.foameraserblue.shop.domain.category.domain.Category

interface CommandCategoryUseCase {
    fun create(title: String, parentId: Long?): Category

    fun update(id: Long, title: String): Category

    fun moveLocation(
        id: Long,
        newParentId: Long?,
        newOrder: Int,
    ): Category

    fun delete(id: Long)
}