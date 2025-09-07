package org.foameraserblue.shop.domain.category.controller

import org.foameraserblue.shop.domain.category.controller.dto.*
import org.foameraserblue.shop.domain.category.service.usecase.CommandCategoryUseCase
import org.foameraserblue.shop.domain.category.service.usecase.QueryCategoryUseCase
import org.springframework.web.bind.annotation.*

/**
 * 테스트/사용성 편의를 위해 파라미터로 id 가 아닌 code 를 사용합니다.
 */
@RestController
@RequestMapping("/categories")
class CategoryController(
    private val queryCategoryUseCase: QueryCategoryUseCase,
    private val commandCategoryUseCase: CommandCategoryUseCase,
) {
    @PostMapping
    fun create(@RequestBody request: CreateCategoryRequest): CategoryResponse {
        return CategoryResponse(
            commandCategoryUseCase.create(
                parentCode = request.parentCode,
                title = request.title,
                code = request.code,
            )
        )
    }

    @GetMapping
    fun getAll(@RequestParam code: String?): CategoryTreeResponse {
        return code
            ?.let { CategoryTreeResponse.toDto(queryCategoryUseCase.getAllMeAndChildrenTree(code)) }
            ?: CategoryTreeResponse.toDto(queryCategoryUseCase.getAllTree())

    }

    @PatchMapping("{code}")
    fun update(@PathVariable code: String, @RequestBody request: UpdateCategoryRequest): CategoryResponse {
        return CategoryResponse(commandCategoryUseCase.patch(code, request.title, request.newCode))
    }

    @PutMapping("{code}/move")
    fun moveLocation(
        @PathVariable code: String,
        @RequestBody request: MoveCategoryRequest,
    ): CategoryResponse {
        return CategoryResponse(
            commandCategoryUseCase.moveParent(code, request.newParentCode)
        )
    }

    @DeleteMapping("{code}")
    fun delete(@PathVariable code: String) {
        commandCategoryUseCase.delete(code)
    }
}