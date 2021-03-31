package trust;

public interface OnCompleteListener<R> {
    void onComplete(Response<R> response);
}
