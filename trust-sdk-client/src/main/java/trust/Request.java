package trust;

import android.net.Uri;
import android.os.Parcelable;

public interface Request extends Parcelable {

    <T> T body();

    Uri key();

    String getAction();

}
