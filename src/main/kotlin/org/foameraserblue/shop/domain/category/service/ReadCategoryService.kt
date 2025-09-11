package org.foameraserblue.shop.domain.category.service

import org.foameraserblue.shop.domain.category.domain.Category
import org.foameraserblue.shop.domain.category.infrastructure.db.CategoryAdapter
import org.foameraserblue.shop.domain.category.service.usecase.ReadCategoryUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ReadCategoryService(
    private val categoryAdapter: CategoryAdapter,
) : ReadCategoryUseCase {
    override fun getAll(): List<Category> {
        return categoryAdapter.findAll()
    }

    // 자신 + 후손 카테고리를 가져옵니다.
    override fun getAllMeAndDescendant(code: String): List<Category> {
        val category = categoryAdapter.findByCode(code)

        return categoryAdapter.findAllByCodeStartingWith(category.code)
    }
}