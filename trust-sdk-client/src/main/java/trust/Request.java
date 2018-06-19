package trust;

import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.Nullable;

public interface Request extends Parcelable {

    <T> T body();

    Uri key();

    @Nullable
    Uri getCallbackUri();

}
