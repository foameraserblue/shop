package org.foameraserblue.shop.domain.category.controller.dto

import org.foameraserblue.shop.domain.category.domain.Category
import org.foameraserblue.shop.domain.category.service.QueryCategoryService
import org.foameraserblue.shop.domain.category.service.usecase.QueryCategoryUseCase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/categories")
class CategoryController (
    private val queryCategoryUseCase: QueryCategoryUseCase,
    private val queryCategoryService: QueryCategoryService,
){
    @GetMapping("root")
    fun test(){
        queryCategoryService.save()
    }


    @GetMapping
    fun getAll(): List<Category>{
        return queryCategoryUseCase.getAll()
    }

    @GetMapping("{id}")
    fun getById(@PathVariable id:Long): Category{
        return queryCategoryUseCase.get(id)
    }
}