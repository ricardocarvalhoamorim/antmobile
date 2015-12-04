package pt.up.fe.infolab.ricardo.antmobile;

/**
 * Created by ricardo on 12/4/15.
 */
import android.content.Context;
import android.content.res.TypedArray;

public class Utils {

    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }
}