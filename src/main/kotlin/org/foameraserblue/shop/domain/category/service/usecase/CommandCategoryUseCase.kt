package org.foameraserblue.shop.domain.category.service.usecase

import org.foameraserblue.shop.domain.category.domain.Category

interface CommandCategoryUseCase {
    fun create(
        parentCode: String?,
        title: String,
        code: String,
    ): Category

    fun patch(
        id: Long,
        title: String?,
        code: String?,
    ): Category

    fun moveParent(
        id: Long,
        newParentCode: String?,
    ): Category

    fun delete(id: Long)
}