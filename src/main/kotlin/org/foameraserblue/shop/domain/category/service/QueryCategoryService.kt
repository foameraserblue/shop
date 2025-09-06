package org.foameraserblue.shop.domain.category.service

import org.foameraserblue.shop.domain.category.domain.Category
import org.foameraserblue.shop.domain.category.infrastructure.db.CategoryAdapter
import org.foameraserblue.shop.domain.category.service.usecase.QueryCategoryUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class QueryCategoryService(
    private val categoryAdapter: CategoryAdapter,
) : QueryCategoryUseCase {
    override fun getAll(): List<Category> {
        return categoryAdapter.findAll()
    }

    override fun getAllWithChildrenById(id: Long): List<Category> {
        val category = categoryAdapter.findById(id)
        return categoryAdapter.findAllByRootIdAndDepthGreaterThanEqual(category.rootId, category.depth)
    }
}