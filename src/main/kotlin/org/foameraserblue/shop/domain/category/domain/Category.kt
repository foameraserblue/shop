package org.foameraserblue.shop.domain.category.domain

import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.foameraserblue.shop.common.entity.BaseLongIdEntity

// 개발 편의를 위해 도메인과 엔티티를 분리하지 않음.
@Entity
@Table(name = "category")
class Category(
    id: Long,

    val title: String,

    /**
     * parent 와 children 을 연관관계로 갖고있는 방식에선 N+1 문제를 해결하기 힘듬
     * 일반적으로 아래로 뻗어나가는 트리구조인데.. 그 경우의 수만큼 쿼리가 나가고 db 터치가 너무 많아진다..
     * 차라리 루트던 상위던 하위던 rootCategoryId 하나로 전부 조회한다음에 자바 코드에서 카테고리 분류를 하는게 낫지않을까?
     * 카테고리는 엄청나게 많은 데이터가 쌓이진 않을것이니 트레이드 오프를 따졌을때 쿼리가 많이 나가는것보다, 필요없는 데이터를 좀 더 가져오는게 성능상 나아보인다
     */
    val rootCategoryId: Long,

    val parentCategoryId: Long?,

    val depth: Int,

    // 같은 depth 의 카테고리끼리의 순서(현 시점에선 화면 출력에 이용함)
    val sortOrderOfSameDepth: Int,
) : BaseLongIdEntity(id) {
    init {
        validateDepth()
        validateRootCategoryConsistency()
        validateDepthAndParentId()
    }

    private fun validateDepth() {
        require(depth >= 0) { "depth 는 음수가 될 수 없습니다." }
    }

    /**
     * 루트 카테고리의 rootCategoryId 일관성 검증.
     * - 루트이고 이미 영속된(id > 0) 상태의 경우: rootCategoryId == id 이어야 한다.
     */
    private fun validateRootCategoryConsistency() {
        if (isRootCategory() && id > 0) {
            require(this.id == this.rootCategoryId) { "루트 카테고리의 rootCategoryId 는 자기 자신의 id 여야합니다." }
        }
    }

    private fun validateDepthAndParentId() {
        if (isRootCategory()) {
            require(depth == 0) { "루트 카테고리의 depth 는 0 이어야 합니다." }
            require(parentCategoryId == null) { "루트 카테고리는 부모 카테고리를 가질 수 없습니다." }
        } else {
            require(depth > 0) { "하위 카테고리의 depth 는 1 이상이어야 합니다." }
            require(parentCategoryId != null) { "루트 카테고리가 아닐경우 부모 카테고리를 필수로 가져야합니다." }
        }
    }

    fun isRootCategory(): Boolean = depth == 0
}


