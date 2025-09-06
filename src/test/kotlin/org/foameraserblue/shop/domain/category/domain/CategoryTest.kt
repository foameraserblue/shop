package org.foameraserblue.shop.domain.category.domain

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec

class CategoryTest : BehaviorSpec({

    Given("루트 카테고리 생성 규칙(depth=0, parent=null)") {

        When("id > 0 인 루트의 rootId 가 자신의 id 와 동일할 때") {
            Then("정상 생성된다") {
                shouldNotThrowAny {
                    Category(
                        id = 10L,
                        title = "상의",
                        rootId = 10L,
                        parentId = null,
                        depth = 0,
                        order = 0
                    )
                }
            }
        }

        When("id > 0 인 루트의 rootId 가 자신의 id 와 다를 때") {
            Then("예외가 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    Category(
                        id = 10L,
                        title = "상의",
                        rootId = 999L,
                        parentId = null,
                        depth = 0,
                        order = 0
                    )
                }
            }
        }

        When("id == 0 (미영속) 루트는 rootId 가 어떤 값이든 허용된다") {
            Then("정상 생성된다") {
                shouldNotThrowAny {
                    Category(
                        id = 0L,
                        title = "상의",
                        rootId = 0L,
                        parentId = null,
                        depth = 0,
                        order = 0
                    )
                }
            }
        }

        When("루트인데 parentId 가 존재하거나 depth != 0 인 경우") {
            Then("예외가 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    Category(
                        id = 1L,
                        title = "상의",
                        rootId = 1L,
                        parentId = 999L,
                        depth = 0,
                        order = 0
                    )
                }

                shouldThrow<IllegalArgumentException> {
                    Category(
                        id = 1L,
                        title = "상의",
                        rootId = 1L,
                        parentId = null,
                        depth = 2,
                        order = 0
                    )
                }
            }
        }
    }

    Given("하위 카테고리 생성 규칙(depth>0, parent!=null)") {

        When("parentId 가 null 이거나 depth 가 0 인 경우") {
            Then("예외가 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    Category(
                        id = 101L,
                        title = "반팔",
                        rootId = 1L,
                        parentId = null,
                        depth = 1,
                        order = 0
                    )
                }

                shouldThrow<IllegalArgumentException> {
                    Category(
                        id = 102L,
                        title = "반팔",
                        rootId = 1L,
                        parentId = 1L,
                        depth = 0,
                        order = 0
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
                        rootId = 1L,
                        parentId = 100L,
                        depth = 2,
                        order = 3
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
                        rootId = 1L,
                        parentId = null,
                        depth = -1,
                        order = 0
                    )
                }
            }
        }
    }
})

