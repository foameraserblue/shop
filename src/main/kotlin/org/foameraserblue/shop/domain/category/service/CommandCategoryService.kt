package org.foameraserblue.shop.domain.category.service

import org.foameraserblue.shop.domain.category.domain.Category
import org.foameraserblue.shop.domain.category.infrastructure.db.CategoryAdapter
import org.foameraserblue.shop.domain.category.service.usecase.CommandCategoryUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CommandCategoryService(
    private val categoryAdapter: CategoryAdapter,
) : CommandCategoryUseCase {
    override fun createCategory(
        title: String,
        parentId: Long?,
    ): Category {
        val category = parentId
            ?.let {
                val parent = categoryAdapter.findById(it)
                val sameParentAndTopOrderCategory = categoryAdapter
                    .findTopByParentIdOrderBySiblingOrderDescOrNull(parent.id)

                Category.createForLeaf(
                    title = title,
                    parentCategory = parent,
                    sameParentAndTopOrderCategory = sameParentAndTopOrderCategory
                )
            }
            ?: run {
                val topOrderRootCategory = categoryAdapter
                    .findTopByParentIdOrderBySiblingOrderDescOrNull(null)

                Category.createForRoot(
                    title = title,
                    topOrderRootCategory = topOrderRootCategory
                )
            }

        validateForCreate(category)

        val saved = categoryAdapter.save(category)

        return if (saved.isRoot()) {
            categoryAdapter.save(saved)
        } else {
            saved
        }
    }

    private fun validateForCreate(category: Category) {
        require(
            !categoryAdapter.existsByParentIdAndSiblingOrder(
                category.parentId,
                category.siblingOrder
            )
        ) { "이미 해당 정렬 순서로 저장된 데이터가 존재합니다." }

    }

    override fun updateCategory(
        id: Long,
        title: String?
    ): Category {
        TODO("Not yet implemented")
    }

    override fun moveCategoryLocation(
        id: Long,
        fromParentId: Long?,
        toParentId: Long?,
        fromSiblingOrder: Int?,
        toSiblingOrder: Int?
    ): Category {
        TODO("Not yet implemented")
    }

    override fun deleteCategory(id: Long) {
        TODO("Not yet implemented")
    }
}