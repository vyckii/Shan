package cis350.upenn.edu.remindmelater;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by stephaniefei on 2/12/17.
 */

public class CustomText extends TextView {
    public CustomText(Context context) {
        super(context);
        init();
    }

    public CustomText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CustomText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/AlegreyaSans-Regular.otf");
        setTypeface(tf);
    }
}
