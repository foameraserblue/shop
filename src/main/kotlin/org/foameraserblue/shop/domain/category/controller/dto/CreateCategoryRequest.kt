package org.foameraserblue.shop.domain.category.controller.dto

data class CreateCategoryRequest(
    val title: String,
    val parentId: Long?,
)
