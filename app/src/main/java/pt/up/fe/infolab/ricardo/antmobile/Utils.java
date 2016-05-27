package pt.up.fe.infolab.ricardo.antmobile;

/**
 * Created by ricardo on 12/4/15.
 */
import android.content.Context;
import android.content.res.TypedArray;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Utils {

    public static String antEndpoint = "http://ant.fe.up.pt/api";
    public static Set<String> antPeople =
            new HashSet<>(Arrays.asList(
                    "http://infolab.fe.up.pt/ontologies/ant#Staff",
                    "http://infolab.fe.up.pt/ontologies/ant#Student"));

    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }
}