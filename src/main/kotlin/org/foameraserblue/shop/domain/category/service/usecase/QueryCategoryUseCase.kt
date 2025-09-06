package org.foameraserblue.shop.domain.category.service.usecase

import org.foameraserblue.shop.domain.category.domain.Category

interface QueryCategoryUseCase {
    fun getAll(): List<Category>

    fun get(id: Long): Category

    fun getRootCategory(id: Long): Category
}