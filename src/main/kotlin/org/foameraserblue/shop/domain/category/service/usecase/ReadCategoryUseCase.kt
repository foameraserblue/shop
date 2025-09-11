package org.foameraserblue.shop.domain.category.service.usecase

import org.foameraserblue.shop.domain.category.domain.Category

interface ReadCategoryUseCase {
    fun getAll(): List<Category>

    fun getAllMeAndDescendant(code: String): List<Category>
}