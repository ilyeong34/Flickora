package com.ilyeong.flickora.core.model

import java.io.IOException

sealed class FlickoraError : IOException() {
    class Network : FlickoraError()
    class Authentication : FlickoraError()
    class NotFound : FlickoraError()
    class Server : FlickoraError()
    class Unknown : FlickoraError()
}
