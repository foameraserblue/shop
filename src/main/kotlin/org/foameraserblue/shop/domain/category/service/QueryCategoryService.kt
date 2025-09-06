package org.foameraserblue.shop.domain.category.service

import org.foameraserblue.shop.domain.category.domain.Category
import org.foameraserblue.shop.domain.category.infrastructure.entitiy.CategoryRepository
import org.foameraserblue.shop.domain.category.service.usecase.QueryCategoryUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class QueryCategoryService(
    private val categoryRepository: CategoryRepository,
) : QueryCategoryUseCase {
    @Transactional
    fun save(){
        val root = Category(
            title = "root",
            parent = null,
            depth = 0,
            sortOrderOfSameDepth = 0,
        )

        val depth1And1 = Category(
            title = "depth1And1",
            parent = root,
            depth = 1,
            sortOrderOfSameDepth = 0,
        )

        val depth1And2 = Category(
            title = "depth1And2",
            parent = root,
            depth = 1,
            sortOrderOfSameDepth = 0,
        )

        root.children.addAll(listOf(depth1And1, depth1And2))

        val depth2And1a = Category(
            title = "depth2And1a",
            parent = depth1And1,
            depth = 2,
            sortOrderOfSameDepth = 0,
        )

        val depth2And1b = Category(
            title = "depth2And1b",
            parent = depth1And1,
            depth = 2,
            sortOrderOfSameDepth = 1,
        )

        depth1And1.children.addAll(listOf(depth2And1a, depth2And1b))

        val depth2And2a = Category(
            title = "depth2And1a",
            parent = depth1And2,
            depth = 2,
            sortOrderOfSameDepth = 0,
        )

        val depth2And2b = Category(
            title = "depth2And1b",
            parent = depth1And2,
            depth = 2,
            sortOrderOfSameDepth = 1,
        )

        depth1And2.children.addAll(listOf(depth2And2a, depth2And2b))

        categoryRepository.save(root)

    }

    override fun getAll(): List<Category> {
        val result= categoryRepository.findAllTest()
        return result
    }

    override fun get(id: Long): Category {
        val result= categoryRepository.findByIdAndParentIsNull(id)!!
//        println(result.children)
        return result
    }

    override fun getRootCategory(id: Long): Category {
        TODO("Not yet implemented")
    }
}