package org.foameraserblue.shop.domain.category.domain

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec

class CategoryTest : BehaviorSpec({

    Given("루트 카테고리 생성 규칙(depth=0, parent=null)") {

        When("id > 0 인 루트의 rootCategoryId 가 자신의 id 와 동일할 때") {
            Then("정상 생성된다") {
                shouldNotThrowAny {
                    Category(
                        id = 10L,
                        title = "상의",
                        rootCategoryId = 10L,
                        parentCategoryId = null,
                        depth = 0,
                        sortOrderOfSameDepth = 0
                    )
                }
            }
        }

        When("id > 0 인 루트의 rootCategoryId 가 자신의 id 와 다를 때") {
            Then("예외가 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    Category(
                        id = 10L,
                        title = "상의",
                        rootCategoryId = 999L,
                        parentCategoryId = null,
                        depth = 0,
                        sortOrderOfSameDepth = 0
                    )
                }
            }
        }

        When("id == 0 (미영속) 루트는 rootCategoryId 가 어떤 값이든 허용된다") {
            Then("정상 생성된다") {
                shouldNotThrowAny {
                    Category(
                        id = 0L,
                        title = "상의",
                        rootCategoryId = 0L,
                        parentCategoryId = null,
                        depth = 0,
                        sortOrderOfSameDepth = 0
                    )
                }
            }
        }

        When("루트인데 parentCategoryId 가 존재하거나 depth != 0 인 경우") {
            Then("예외가 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    Category(
                        id = 1L,
                        title = "상의",
                        rootCategoryId = 1L,
                        parentCategoryId = 999L,
                        depth = 0,
                        sortOrderOfSameDepth = 0
                    )
                }

                shouldThrow<IllegalArgumentException> {
                    Category(
                        id = 1L,
                        title = "상의",
                        rootCategoryId = 1L,
                        parentCategoryId = null,
                        depth = 2,
                        sortOrderOfSameDepth = 0
                    )
                }
            }
        }
    }

    Given("하위 카테고리 생성 규칙(depth>0, parent!=null)") {

        When("parentCategoryId 가 null 이거나 depth 가 0 인 경우") {
            Then("예외가 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    Category(
                        id = 101L,
                        title = "반팔",
                        rootCategoryId = 1L,
                        parentCategoryId = null,
                        depth = 1,
                        sortOrderOfSameDepth = 0
                    )
                }

                shouldThrow<IllegalArgumentException> {
                    Category(
                        id = 102L,
                        title = "반팔",
                        rootCategoryId = 1L,
                        parentCategoryId = 1L,
                        depth = 0,
                        sortOrderOfSameDepth = 0
                    )
                }
            }
        }

        When("정상적인 하위 카테고리를 생성할 때(depth>0 && parent!=null)") {
            Then("정상 생성된다") {
                shouldNotThrowAny {
                    Category(
                        id = 200L,
                        title = "면티",
                        rootCategoryId = 1L,
                        parentCategoryId = 100L,
                        depth = 2,
                        sortOrderOfSameDepth = 3
                    )
                }
            }
        }
    }

    Given("depth 유효성") {
        When("depth 가 음수일 때") {
            Then("예외가 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    Category(
                        id = 1L,
                        title = "바지",
                        rootCategoryId = 1L,
                        parentCategoryId = null,
                        depth = -1,
                        sortOrderOfSameDepth = 0
                    )
                }
            }
        }
    }
})

