package org.foameraserblue.shop.common.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@MappedSuperclass
class BaseLongIdEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
) {
    @CreationTimestamp
    @Column(nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    var updatedAt: LocalDateTime? = null

    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }
}