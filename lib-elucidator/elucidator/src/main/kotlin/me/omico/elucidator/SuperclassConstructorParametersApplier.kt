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

import com.squareup.kotlinpoet.TypeSpec

public inline fun <reified T> TypeScope.superclass(block: SuperclassConstructorParametersScope.() -> Unit) {
    superclass<T>()
    SuperclassConstructorParametersApplier(builder).apply(block)
}

public interface SuperclassConstructorParametersScope {
    public val builder: TypeSpec.Builder
}

public fun SuperclassConstructorParametersScope.addParameter(format: String, vararg args: Any) {
    builder.addSuperclassConstructorParameter(format = format, args = args)
}

public class SuperclassConstructorParametersApplier(
    override val builder: TypeSpec.Builder,
) : SuperclassConstructorParametersScope
