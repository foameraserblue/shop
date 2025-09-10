package org.foameraserblue.shop.domain.category.controller

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.foameraserblue.shop.domain.category.controller.dto.CategoryResponse
import org.foameraserblue.shop.domain.category.controller.dto.CategoryTreeResponse
import org.foameraserblue.shop.support.mapper
import org.foameraserblue.shop.support.readBody
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class CategoryControllerTest(val mockMvc: MockMvc) : BehaviorSpec({
    data class CreateCategoryRequest(val parentCode: String, val title: String, val segmentCode: String)
    data class UpdateCategoryRequest(val title: String, val newSegmentCode: String)

    fun Any.toJson(): String = mapper.writeValueAsString(this)

    Given("카테고리 REST API 통합 시나리오") {
        When("루트 카테고리(001 상의)를 생성하려한다") {
            val req = CreateCategoryRequest(parentCode = "", title = "상의", segmentCode = "001")

            Then("응답 성공") {
                val mvcRes = mockMvc.perform(
                    post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(req.toJson())
                )
                    .andExpect(status().isOk)
                    .andReturn()

                val body = readBody<CategoryResponse>(mvcRes.response.contentAsString)
                body.title.shouldBe("상의")
                body.code.shouldBe("001")
            }
        }

        When("자식 카테고리(001002 티셔츠)를 생성한다") {
            val req = CreateCategoryRequest(parentCode = "001", title = "티셔츠", segmentCode = "002")

            Then("응답 성공") {
                val mvcRes = mockMvc.perform(
                    post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(req.toJson())
                )
                    .andExpect(status().isOk)
                    .andReturn()

                val body = readBody<CategoryResponse>(mvcRes.response.contentAsString)
                body.title.shouldBe("티셔츠")
                body.code.shouldBe("001002")
            }
        }

        When("중복 루트 코드(001)로 다시 생성 요청한다") {
            val dup = CreateCategoryRequest(parentCode = "", title = "상의-중복", segmentCode = "001")

            Then("400 Bad Request 반환") {
                mockMvc.perform(
                    post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dup.toJson())
                )
                    .andExpect(status().isBadRequest)
            }
        }

        When("전체 트리를 조회한다 (code 미지정)") {
            Then("응답 성공") {
                val mvcRes = mockMvc.perform(get("/categories"))
                    .andExpect(status().isOk)
                    .andReturn()

                val tree = readBody<CategoryTreeResponse>(mvcRes.response.contentAsString)
                tree.data.list.shouldHaveAtLeastSize(1)
                tree.data.list.first().code.shouldBe("001")
                tree.data.list.first().title.shouldBe("상의")
            }
        }

        When("특정 코드(001) 기준 자신+후손 트리를 조회한다") {
            Then("응답 성공") {
                val mvcRes = mockMvc.perform(get("/categories").param("code", "001"))
                    .andExpect(status().isOk)
                    .andReturn()

                val tree = readBody<CategoryTreeResponse>(mvcRes.response.contentAsString)
                tree.data.list.shouldHaveAtLeastSize(1)

                val root = tree.data.list.first()
                root.code.shouldBe("001")
                root.childrenList.shouldHaveAtLeastSize(1)

                val child = root.childrenList.first()
                child.code.shouldBe("001002")
                child.title.shouldBe("티셔츠")
            }
        }

        When("자식(001002)을 001003(셔츠)로 변경한다") {
            val req = UpdateCategoryRequest(title = "셔츠", newSegmentCode = "003")

            Then("응답 성공") {
                val mvcRes = mockMvc.perform(
                    put("/categories/{code}", "001002")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(req.toJson())
                )
                    .andExpect(status().isOk)
                    .andReturn()

                val body = readBody<CategoryResponse>(mvcRes.response.contentAsString)
                body.title.shouldBe("셔츠")
                body.code.shouldBe("001003")
            }
        }

        When("변경된 결과(001 기준 트리)를 조회한다") {
            Then("응답 성공") {
                val mvcRes = mockMvc.perform(get("/categories").param("code", "001"))
                    .andExpect(status().isOk)
                    .andReturn()

                val tree = readBody<CategoryTreeResponse>(mvcRes.response.contentAsString)

                val root = tree.data.list.firstOrNull().shouldNotBeNull()
                root.childrenList.firstOrNull().shouldNotBeNull().apply {
                    code.shouldBe("001003")
                    title.shouldBe("셔츠")
                }
            }
        }

        When("루트(001)를 삭제한다") {
            Then("응답 성공") {
                mockMvc.perform(delete("/categories/{code}", "001"))
                    .andExpect(status().isOk)
            }
        }

        When("삭제 후 전체 트리를 조회한다") {
            Then("응답 성공") {
                val mvcRes = mockMvc.perform(get("/categories"))
                    .andExpect(status().isOk)
                    .andReturn()

                val tree = readBody<CategoryTreeResponse>(mvcRes.response.contentAsString)

                val has001AtTop = tree.data.list.any { it.code == "001" }
                has001AtTop.shouldBe(false)
            }
        }
    }
})