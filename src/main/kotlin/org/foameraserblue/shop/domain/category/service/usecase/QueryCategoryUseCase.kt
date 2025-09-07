package org.foameraserblue.shop.domain.category.service.usecase

import org.foameraserblue.shop.domain.category.domain.CategoryTree

interface QueryCategoryUseCase {
    fun getAllTree(): List<CategoryTree>

    fun getAllMeAndChildrenTree(code: String): CategoryTree
}