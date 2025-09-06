package org.foameraserblue.shop.domain.category.service.usecase

import org.foameraserblue.shop.domain.category.domain.Category

interface QueryCategoryUseCase {
    fun getAll(): List<Category>

    fun getAllWithChildrenNodeById(id: Long): List<Category>

    fun getAllRootWithChildrenNodeById(id: Long): List<Category>
}