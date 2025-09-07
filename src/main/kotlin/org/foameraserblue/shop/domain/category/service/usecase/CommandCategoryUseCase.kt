package org.foameraserblue.shop.domain.category.service.usecase

import org.foameraserblue.shop.domain.category.domain.Category

interface CommandCategoryUseCase {
    fun create(
        parentCode: String?,
        title: String,
        code: String,
    ): Category

    fun patch(
        code: String,
        title: String?,
        newCode: String?,
    ): Category

    fun moveParent(
        code: String,
        newParentCode: String?,
    ): Category

    fun delete(code: String)
}