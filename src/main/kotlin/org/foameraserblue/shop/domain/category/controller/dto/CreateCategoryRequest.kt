package org.foameraserblue.shop.domain.category.controller.dto

data class CreateCategoryRequest(
    val parentCode: String,
    val title: String,
    val segmentCode: String,
)
