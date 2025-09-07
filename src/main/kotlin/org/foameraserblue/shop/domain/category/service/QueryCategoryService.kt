package org.foameraserblue.shop.domain.category.service

import org.foameraserblue.shop.domain.category.domain.CategoryTree
import org.foameraserblue.shop.domain.category.infrastructure.db.CategoryAdapter
import org.foameraserblue.shop.domain.category.service.usecase.QueryCategoryUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class QueryCategoryService(
    private val categoryAdapter: CategoryAdapter,
) : QueryCategoryUseCase {
    override fun getAllTree(): List<CategoryTree> {
        val categories = categoryAdapter.findAll()

        return CategoryTree.getAllTree(categories)
    }

    override fun getAllMeAndChildrenTree(code: String): CategoryTree {
        val category = categoryAdapter.findByCode(code)
        val candidates =
            categoryAdapter.findAllByRootCodeAndDepthGreaterThanEqual(category.rootCode, category.depth)

        return CategoryTree.getAllMeAndDescendantsTree(candidates, category.code)
    }
}