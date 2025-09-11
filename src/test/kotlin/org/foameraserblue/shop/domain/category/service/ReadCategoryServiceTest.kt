package org.foameraserblue.shop.domain.category.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.foameraserblue.shop.common.exception.NotFoundException
import org.foameraserblue.shop.domain.category.domain.Category
import org.foameraserblue.shop.domain.category.infrastructure.db.CategoryAdapter

class ReadCategoryServiceTest : BehaviorSpec({
    lateinit var adapter: CategoryAdapter
    lateinit var service: ReadCategoryService

    beforeSpec {
        adapter = mockk(relaxed = true)
        service = ReadCategoryService(adapter)
    }

    beforeContainer {
        clearMocks(adapter, answers = false)
    }

    Given("getAll 테스트") {
        When("모든 카테고리를 조회하면") {
            val list = listOf(
                Category(title = "상의", code = "001"),
                Category(title = "하의", code = "002"),
            )
            every { adapter.findAll() } returns list

            Then("findAll 이 호출된다") {
                val result = service.getAll()

                result.shouldBe(list)
                verify(exactly = 1) { adapter.findAll() }
            }
        }
    }

    Given("getAllMeAndDescendant 테스트") {
        When("코드로 자신 + 후손을 조회하면") {
            val me = Category(title = "상의", code = "001")
            val descendants = listOf(
                me,
                Category(title = "티셔츠", code = "001002"),
                Category(title = "반팔", code = "001002003"),
            )

            every { adapter.findByCode("001") } returns me
            every { adapter.findAllByCodeStartingWith("001") } returns descendants

            Then("자신 + 후손의 리스트가 반환된다") {
                val result = service.getAllMeAndDescendant("001")

                result.shouldBe(descendants)

                verify(exactly = 1) { adapter.findByCode("001") }
                verify(exactly = 1) { adapter.findAllByCodeStartingWith("001") }
            }
        }

        When("코드에 해당하는 카테고리가 없으면") {
            every { adapter.findByCode("999") } throws NotFoundException("999 code의 카테고리가 존재하지않습니다.")

            Then("예외를 뱉는다") {
                shouldThrow<NotFoundException> {
                    service.getAllMeAndDescendant("999")
                }
            }
        }
    }
})