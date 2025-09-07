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

    val depth: Int,

    val rootCode: String,

    val parentCode: String?,

    val code: String,
) : BaseLongIdEntity(id) {
    constructor(category: Category) : this(
        id = category.id,
        title = category.title,
        rootCode = category.rootCode,
        depth = category.depth,
        parentCode = category.parentCode,
        code = category.code,
    )

    fun toDomain() = Category(
        id = id,
        title = title,
        rootCode = rootCode,
        depth = depth,
        parentCode = parentCode,
        code = code
    )
}