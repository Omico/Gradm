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

public interface ConditionalStatementScope {
    public val scope: FunctionScope
    public fun begin(condition: String, vararg args: Any, block: FunctionScope.() -> Unit)
    public fun next(condition: String, vararg args: Any, block: FunctionScope.() -> Unit)
    public fun end()
}

internal class ConditionalStatementBuilder(
    override val scope: FunctionScope,
) : ConditionalStatementScope {
    override fun begin(condition: String, vararg args: Any, block: FunctionScope.() -> Unit) {
        scope.builder.beginControlFlow(condition, *args)
        scope.block()
    }

    override fun next(condition: String, vararg args: Any, block: FunctionScope.() -> Unit) {
        scope.builder.nextControlFlow(condition, *args)
        scope.block()
    }

    override fun end() {
        scope.builder.endControlFlow()
    }
}
