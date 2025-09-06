package org.foameraserblue.shop.domain.category.controller

import org.foameraserblue.shop.domain.category.controller.dto.CategoryResponse
import org.foameraserblue.shop.domain.category.controller.dto.CategoryTreeResponse
import org.foameraserblue.shop.domain.category.controller.dto.CreateCategoryRequest
import org.foameraserblue.shop.domain.category.service.usecase.CommandCategoryUseCase
import org.foameraserblue.shop.domain.category.service.usecase.QueryCategoryUseCase
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/categories")
class CategoryController(
    private val queryCategoryUseCase: QueryCategoryUseCase,
    private val commandCategoryUseCase: CommandCategoryUseCase,
) {
    @GetMapping
    fun getAll(@RequestParam id: Long?): CategoryTreeResponse {
        return CategoryTreeResponse.toDto(
            id
                ?.let { queryCategoryUseCase.getAllWithChildrenNodeById(id) }
                ?: queryCategoryUseCase.getAll())
    }

    @PostMapping
    fun create(@RequestBody request: CreateCategoryRequest): CategoryResponse {
        return CategoryResponse(
            commandCategoryUseCase.createCategory(
                title = request.title,
                parentId = request.parentId,
            )
        )
    }
}