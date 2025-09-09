package org.foameraserblue.shop.support

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule

val mapper: ObjectMapper = jsonMapper { addModule(kotlinModule()) }

// 응답 본문을 T로 역직렬화하는 헬퍼
inline fun <reified T> readBody(content: String): T = mapper.readValue(content, T::class.java)