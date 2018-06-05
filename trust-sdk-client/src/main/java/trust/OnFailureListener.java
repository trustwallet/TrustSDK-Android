package trust;

public interface OnFailureListener {
    void onFail(Request request, int error);
}
