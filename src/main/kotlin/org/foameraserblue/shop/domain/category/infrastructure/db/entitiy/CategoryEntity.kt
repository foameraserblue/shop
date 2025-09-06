package org.foameraserblue.shop.domain.category.infrastructure.db.entitiy

import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.foameraserblue.shop.common.entity.BaseLongIdEntity
import org.foameraserblue.shop.domain.category.domain.Category

@Entity
@Table(name = "category")
class CategoryEntity(
    id: Long,

    val title: String,

    val rootCategoryId: Long,

    val parentCategoryId: Long?,

    val depth: Int,

    val sortOrderOfSameDepth: Int,
) : BaseLongIdEntity(id) {
    constructor(category: Category) : this(
        id = category.id,
        title = category.title,
        rootCategoryId = category.rootCategoryId,
        parentCategoryId = category.parentCategoryId,
        depth = category.depth,
        sortOrderOfSameDepth = category.sortOrderOfSameDepth,
    )

    fun toDomain() = Category(
        id = id,
        title = title,
        rootCategoryId = rootCategoryId,
        parentCategoryId = parentCategoryId,
        depth = depth,
        sortOrderOfSameDepth = sortOrderOfSameDepth,
    )
}