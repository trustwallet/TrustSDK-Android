package trust

class Response<R>(val request: Request<R>?, val result: R?, val error: Error) {
    val isSuccess: Boolean
        get() = error == Error.NONE

    val isAvailable: Boolean
        get() = request != null && error > Error.NONE

}