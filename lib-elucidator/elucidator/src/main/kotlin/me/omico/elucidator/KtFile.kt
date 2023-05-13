/*
 * Copyright 2023 Omico
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.omico.elucidator

import com.squareup.kotlinpoet.FileSpec
import java.nio.file.Path

fun ktFile(
    packageName: String = "",
    fileName: String,
    block: KtFileScope.() -> Unit,
): FileSpec =
    FileSpec.builder(packageName, fileName).applyDslBuilder(block).build()

fun KtFileScope.addFunction(name: String, block: FunctionScope.() -> Unit) {
    function(name, block).let(builder::addFunction)
}

fun KtFileScope.addObjectType(name: String, block: TypeScope.() -> Unit) {
    objectType(name, block).let(builder::addType)
}

inline fun <reified T : Annotation> KtFileScope.addAnnotation(noinline block: AnnotationScope.() -> Unit) {
    annotation(T::class, block).let(builder::addAnnotation)
}

fun KtFileScope.addFileComment(format: String, vararg args: Any) {
    builder.addFileComment(format, *args)
}

fun KtFileScope.writeTo(path: Path) = build().writeTo(path)
