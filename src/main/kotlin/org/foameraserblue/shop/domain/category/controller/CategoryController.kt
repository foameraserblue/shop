package org.foameraserblue.shop.domain.category.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.foameraserblue.shop.domain.category.controller.dto.CategoryResponse
import org.foameraserblue.shop.domain.category.controller.dto.CategoryTreeResponse
import org.foameraserblue.shop.domain.category.controller.dto.CreateCategoryRequest
import org.foameraserblue.shop.domain.category.controller.dto.UpdateCategoryRequest
import org.foameraserblue.shop.domain.category.service.usecase.CategoryUseCase
import org.foameraserblue.shop.domain.category.service.usecase.ReadCategoryUseCase
import org.springframework.web.bind.annotation.*

@Tag(name = "카테고리 컨트롤러", description = "카테고리 컨트롤러")
@RestController
@RequestMapping("/categories")
class CategoryController(
    private val readCategoryUseCase: ReadCategoryUseCase,
    private val categoryUseCase: CategoryUseCase,
) {
    @Operation(description = "카테고리 생성")
    @PostMapping
    fun create(@RequestBody request: CreateCategoryRequest): CategoryResponse {
        return CategoryResponse(
            categoryUseCase.create(
                parentCode = request.parentCode,
                title = request.title,
                segmentCode = request.segmentCode,
            )
        )
    }

    @Operation(description = "카테고리 조회")
    @GetMapping
    fun getAll(@RequestParam code: String?): CategoryTreeResponse {
        return code
            ?.let { CategoryTreeResponse.toDto(readCategoryUseCase.getAllMeAndDescendant(code)) }
            ?: CategoryTreeResponse.toDto(readCategoryUseCase.getAll())

    }

    @Operation(description = "카테고리 수정")
    @PutMapping("{code}")
    fun update(@PathVariable code: String, @RequestBody request: UpdateCategoryRequest): CategoryResponse {
        return CategoryResponse(categoryUseCase.update(code, request.title, request.newSegmentCode))
    }

    @Operation(description = "카테고리 삭제")
    @DeleteMapping("{code}")
    fun delete(@PathVariable code: String) {
        categoryUseCase.delete(code)
    }
}