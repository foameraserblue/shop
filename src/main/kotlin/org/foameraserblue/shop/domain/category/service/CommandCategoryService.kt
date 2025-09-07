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
    override fun create(
        title: String,
        parentId: Long?,
    ): Category {
        val category = parentId
            ?.let {
                createLeafCategory(title, parentId)
            }
            ?: run {
                createRootCategory(title)
            }

//        validateForCreate(category)
        val saved = categoryAdapter.save(category)

        return if (saved.isRoot) {
            saved.rootIdInitialize()
            categoryAdapter.save(saved)
        } else {
            saved
        }
    }

    private fun createLeafCategory(title: String, parentId: Long): Category {
        val parent = categoryAdapter.findById(parentId)

        return Category.createForLeaf(
            title = title,
            parentCategory = parent,
        )
    }

    private fun createRootCategory(title: String): Category {
        return Category.createForRoot(
            title = title,
        )
    }

//    private fun validateForCreate(category: Category) {
//        require(
//            !categoryAdapter.existsByParentIdAndOrder(
//                category.parentId,
//                category.order
//            )
//        ) { "이미 해당 정렬 순서로 저장된 데이터가 존재합니다." }
//    }

    override fun update(
        id: Long,
        title: String
    ): Category {
        val category = categoryAdapter.findById(id)

        return categoryAdapter.save(category.update(title))
    }

    override fun moveParent(
        id: Long,
        newParentId: Long?,
    ): Category {
        val category = categoryAdapter.findById(id)

        if (category.parentId != newParentId) {
            val newParent = newParentId?.let { categoryAdapter.findById(newParentId) }
            category.moveParent(newParent)
        }

        return categoryAdapter.save(category)
    }

    override fun delete(id: Long) {
        val category = categoryAdapter.findById(id)
        val categoryAndChildren =
            categoryAdapter.findAllByRootIdAndDepthGreaterThanEqual(category.rootId, category.depth)

        categoryAdapter.deleteAll(categoryAndChildren)
    }
}