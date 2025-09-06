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

    val rootId: Long,

    val parentId: Long?,

    val depth: Int,

    val siblingOrder: Int,
) : BaseLongIdEntity(id) {
    constructor(category: Category) : this(
        id = category.id,
        title = category.title,
        rootId = category.rootId,
        parentId = category.parentId,
        depth = category.depth,
        siblingOrder = category.siblingOrder,
    )

    fun toDomain() = Category(
        id = id,
        title = title,
        rootId = rootId,
        parentId = parentId,
        depth = depth,
        siblingOrder = siblingOrder,
    )
}