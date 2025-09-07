package org.foameraserblue.shop.domain.category.controller.dto

import org.foameraserblue.shop.domain.category.domain.Category

data class CategoryResponse(
    val id: Long,
    val title: String,
    val depth: Int,
    val categoryCode: String,
    val parentCode: String?,
) {
    constructor(category: Category) : this(
        id = category.id,
        title = category.title,
        depth = category.depth,
        categoryCode = category.code,
        parentCode = category.parentCode,
    )
}