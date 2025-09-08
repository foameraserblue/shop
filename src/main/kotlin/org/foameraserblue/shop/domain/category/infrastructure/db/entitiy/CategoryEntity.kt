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
    val code: String,
) : BaseLongIdEntity(id) {
    constructor(category: Category) : this(
        id = category.id,
        title = category.title,
        code = category.code,
    )

    fun toDomain() = Category(
        id = id,
        title = title,
        code = code
    )
}