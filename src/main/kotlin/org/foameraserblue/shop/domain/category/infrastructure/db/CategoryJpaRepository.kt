package org.foameraserblue.shop.domain.category.infrastructure.db

import org.foameraserblue.shop.domain.category.infrastructure.db.entitiy.CategoryEntity
import org.springframework.data.jpa.repository.JpaRepository

/**
 * 변경이나 수정이 쉽지않은 도메인이라 생각함.
 *
 * 카테고리 순서나 이름의 수정, 삭제를 API 로 할 경우가 얼마나있을까?
 * 아마 카테고리 id 면 상품에 매핑돼있을텐데,
 * 나였으면 운영환경에 변경할때 DB 마이그레이션 했을것같음.
 * 이것도 문제인게 마이그레이션 전략도 꽤 복잡해짐.
 *
 * API 로 삭제하면 이벤트 날려서 상품의 category_id 외래키도 전부 다른 category_id 로 바꿔줘야함.
 * 이럴경우 상품에서 오류가났을때 보상 트랜잭션도 만들어야되고 사실 무척 복잡한 도메인이라고 생각함.
 *
 * 하지만 API 로 변경할수 있다는 가정하에, 여러 스레드에서 동시에 변경했을때를 위해 락을 걸어야될수도.
 */
interface CategoryJpaRepository : JpaRepository<CategoryEntity, Long> {
    fun findAllByRootId(rootId: Long): List<CategoryEntity>

    fun existsByParentIdAndOrder(parentId: Long?, order: Int): Boolean

    fun findTopByParentIdOrderByOrderDesc(parentId: Long?): CategoryEntity?

    fun findAllByParentId(parentId: Long?): List<CategoryEntity>
}