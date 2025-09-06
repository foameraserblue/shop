package org.foameraserblue.shop.domain.category.service.usecase

import org.foameraserblue.shop.domain.category.domain.Category

interface QueryCategoryUseCase {
    fun getAll(): List<Category>

    fun getAllWithChildrenById(id: Long): List<Category>

    fun getAllRootWithChildrenById(id: Long): List<Category>
}