package org.foameraserblue.shop.domain.category.service

import org.foameraserblue.shop.domain.category.domain.Category
import org.foameraserblue.shop.domain.category.infrastructure.db.CategoryAdapter
import org.foameraserblue.shop.domain.category.service.usecase.CategoryUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CategoryService(
    private val categoryAdapter: CategoryAdapter,
) : CategoryUseCase {
    override fun create(
        parentCode: String,
        title: String,
        segmentCode: String,
    ): Category {
        val category =
            if (parentCode.isBlank()) {
                // 루트 카테고리 생성조건
                Category.createForRoot(title = title, rootSegment = segmentCode)
            } else {
                // 리프 카테고리 생성 조건
                val parent = categoryAdapter.findByCode(parentCode)

                Category.createForLeaf(parent = parent, title = title, segment = segmentCode)
            }

        validateExistsCode(category.code)

        return categoryAdapter.save(category)
    }

    override fun update(
        code: String,
        title: String,
        newSegmentCode: String,
    ): Category {
        val category = categoryAdapter.findByCode(code)
        val oldPrefix = category.code

        category.update(title, newSegmentCode)
        val newPrefix = category.code

        if (oldPrefix == newPrefix) {
            return categoryAdapter.save(category)
        }

        validateExistsCode(newPrefix)

        // 후손 카테고리의 code prefix 를 변경해줍니다
        rebaseDescendants(oldPrefix, newPrefix)

        return categoryAdapter.save(category)

    }

    // 변경된 prefix 로 후손들의 코드 베이스를 변경해줍니다
    private fun rebaseDescendants(oldPrefix: String, newPrefix: String) {
        val descendants = categoryAdapter
            .findAllByCodeStartingWithAndCodeNot(oldPrefix, oldPrefix)
        descendants.forEach { it.rebaseWithParent(oldPrefix, newPrefix) }

        categoryAdapter.saveAll(descendants)
    }


    private fun validateExistsCode(code: String) {
        require(
            !categoryAdapter.existsByCode(code)
        ) { "이미 해당 code로 저장된 데이터가 존재합니다." }
    }

    override fun delete(code: String) {
        val category = categoryAdapter.findByCode(code)

        categoryAdapter.deleteAllByCodeStartingWith(category.code)
    }
}