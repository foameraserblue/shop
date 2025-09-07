package org.foameraserblue.shop.domain.category.controller.dto

import org.foameraserblue.shop.domain.category.domain.Category

data class CategoryResponse (
    val id: Long,
    val title: String,
    val parentId: Long?,
    val depth: Int,
){
    constructor(category: Category): this(
        id = category.id,
        title = category.title,
        parentId = category.parentId,
        depth = category.depth,
    )
}