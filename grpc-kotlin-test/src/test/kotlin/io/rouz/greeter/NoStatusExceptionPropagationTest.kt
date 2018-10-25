/*-
 * -\-\-
 * grpc-kotlin-test
 * --
 * Copyright (C) 2016 - 2018 rouz.io
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */

package io.rouz.greeter

import kotlinx.coroutines.experimental.channels.ProducerScope
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import org.junit.After

class NoStatusExceptionPropagationTest : StatusExceptionTestBase() {

    override val service: GreeterGrpcKt.GreeterImplBase
        get() = StatusThrowingGreeter()

    @After
    fun tearDown() {
        assert(seenExceptions.isEmpty()) {
            "Status exceptions should not reach context handler"
        }
    }

    private inner class StatusThrowingGreeter : GreeterGrpcKt.GreeterImplBase(collectExceptions) {

        override suspend fun greet(request: GreetRequest): GreetReply {
            throw notFound("uni")
        }

        override suspend fun ProducerScope<GreetReply>.greetServerStream(request: GreetRequest) {
            throw notFound("sstream")
        }

        override suspend fun greetClientStream(requestChannel: ReceiveChannel<GreetRequest>): GreetReply {
            throw notFound("cstream")
        }

        override suspend fun ProducerScope<GreetReply>.greetBidirectional(requestChannel: ReceiveChannel<GreetRequest>) {
            throw notFound("bidi")
        }
    }
}
