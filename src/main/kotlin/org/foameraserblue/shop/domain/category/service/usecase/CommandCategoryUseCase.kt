package org.foameraserblue.shop.domain.category.service.usecase

import org.foameraserblue.shop.domain.category.domain.Category

interface CommandCategoryUseCase {
    fun create(
        parentCode: String,
        title: String,
        segmentCode: String,
    ): Category

    fun update(
        code: String,
        title: String,
        newSegmentCode: String,
    ): Category

    fun delete(code: String)
}