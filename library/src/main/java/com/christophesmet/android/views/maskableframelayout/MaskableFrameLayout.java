package com.christophesmet.android.views.maskableframelayout;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.content.res.AppCompatResources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.christophesmet.android.view.maskablelayout.R;

/**
 * Created by Christophe on 12/07/2014.
 */

public class MaskableFrameLayout extends FrameLayout {

    //Constants
    private static final String TAG = "MaskableFrameLayout";

    private Handler mHandler;

    //Mask props
    @Nullable
    private Drawable mDrawableMask = null;
    @Nullable
    private Bitmap mFinalMask = null;

    //Drawing props
    private Paint mPaint = null;
    private PorterDuffXfermode mPorterDuffXferMode = null;

    public MaskableFrameLayout(Context context) {
        super(context);
    }

    public MaskableFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        construct(context, attrs);
    }

    public MaskableFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        construct(context, attrs);
    }

    private void construct(Context context, AttributeSet attrs) {
        mHandler = new Handler();
        setDrawingCacheEnabled(true);
        setLayerType(LAYER_TYPE_SOFTWARE, null); //Only works for software layers
        mPaint = createPaint(false);
        Resources.Theme theme = context.getTheme();
        if (theme != null) {
            TypedArray a = theme.obtainStyledAttributes(
                    attrs,
                    R.styleable.MaskableLayout,
                    0, 0);
            try {
                //Load the mask if specified in xml
                initMask(loadMask(a));
                //Load the mode if specified in xml
                mPorterDuffXferMode = getModeFromInteger(
                        a.getInteger(R.styleable.MaskableLayout_porterduffxfermode, 0));
                initMask(mDrawableMask);
                //Check antiAlias
                if (a.getBoolean(R.styleable.MaskableLayout_anti_aliasing, false)) {
                    //Recreate paint with anti aliasing enabled
                    //This can take a performance hit.
                    mPaint = createPaint(true);
                }
            } finally {
                if (a != null) {
                    a.recycle();
                }
            }
        } else {
            log("Couldn't load theme, mask in xml won't be loaded.");
        }
        registerMeasure();
    }

    @NonNull
    private Paint createPaint(boolean antiAliasing) {
        Paint output = new Paint(Paint.ANTI_ALIAS_FLAG);
        output.setAntiAlias(antiAliasing);
        output.setXfermode(mPorterDuffXferMode);
        return output;
    }

    //Mask functions
    @Nullable
    private Drawable loadMask(@NonNull TypedArray a) {
        final int drawableResId = a.getResourceId(R.styleable.MaskableLayout_mask, -1);
        if (drawableResId == -1) {
            return null;
        }
        return AppCompatResources.getDrawable(getContext(), drawableResId);
    }

    private void initMask(@Nullable Drawable input) {
        if (input != null) {
            mDrawableMask = input;
            if (mDrawableMask instanceof AnimationDrawable) {
                mDrawableMask.setCallback(this);
            }
        } else {
            log("Are you sure you don't want to provide a mask ?");
        }
    }

    @Nullable
    public Drawable getDrawableMask() {
        return mDrawableMask;
    }

    @Nullable
    private Bitmap makeBitmapMask(@Nullable Drawable drawable) {
        if (drawable != null) {
            if (getMeasuredWidth() > 0 && getMeasuredHeight() > 0) {
                Bitmap mask = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(),
                        Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(mask);
                drawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                drawable.draw(canvas);
                return mask;
            } else {
                log("Can't create a mask with height 0 or width 0. Or the layout has no children and is wrap content");
                return null;
            }
        } else {
            log("No bitmap mask loaded, view will NOT be masked !");
        }
        return null;
    }

    public void setMask(int drawableRes) {
        Resources res = getResources();
        if (res != null) {
            setMask(res.getDrawable(drawableRes));
        } else {
            log("Unable to load resources, mask will not be loaded as drawable");
        }
    }

    public void setMask(@Nullable Drawable input) {
        initMask(input);
        swapBitmapMask(makeBitmapMask(mDrawableMask));
        invalidate();
    }

    public void setPorterDuffXferMode(PorterDuff.Mode mode) {
        this.mPorterDuffXferMode = new PorterDuffXfermode(mode);
    }

    //Once the size has changed we need to remake the mask.
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setSize(w, h);
    }

    private void setSize(int width, int height) {
        if (width > 0 && height > 0) {
            if (mDrawableMask != null) {
                //Remake the 9patch
                swapBitmapMask(makeBitmapMask(mDrawableMask));
            }
        } else {
            log("Width and height must be higher than 0");
        }
    }

    //Drawing
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mFinalMask != null && mPaint != null) {
            mPaint.setXfermode(mPorterDuffXferMode);
            canvas.drawBitmap(mFinalMask, 0.0f, 0.0f, mPaint);
            mPaint.setXfermode(null);
        } else {
            log("Mask or paint is null ...");
        }
    }

    //Once inflated we have no height or width for the mask. Wait for the layout.
    private void registerMeasure() {
        final ViewTreeObserver treeObserver = MaskableFrameLayout.this.getViewTreeObserver();
        if (treeObserver != null && treeObserver.isAlive()) {
            treeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    ViewTreeObserver aliveObserver = treeObserver;
                    if (!aliveObserver.isAlive()) {
                        aliveObserver = MaskableFrameLayout.this.getViewTreeObserver();
                    }
                    if (aliveObserver != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            aliveObserver.removeOnGlobalLayoutListener(this);
                        } else {
                            aliveObserver.removeGlobalOnLayoutListener(this);
                        }
                    } else {
                        log("GlobalLayoutListener not removed as ViewTreeObserver is not valid");
                    }
                    swapBitmapMask(makeBitmapMask(mDrawableMask));
                }
            });
        }
    }

    //Logging
    private void log(@NonNull String message) {
        Log.d(TAG, message);
    }

    //Animation
    @Override
    public void invalidateDrawable(Drawable dr) {
        if (dr != null) {
            initMask(dr);
            swapBitmapMask(makeBitmapMask(dr));
            invalidate();
        }
    }

    @Override
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        if (who != null && what != null) {
            mHandler.postAtTime(what, when);
        }
    }

    @Override
    public void unscheduleDrawable(Drawable who, Runnable what) {
        if (who != null && what != null) {
            mHandler.removeCallbacks(what);
        }
    }

    private void swapBitmapMask(@Nullable Bitmap newMask) {
        if (newMask != null) {
            if (mFinalMask != null && !mFinalMask.isRecycled()) {
                mFinalMask.recycle();
            }
            mFinalMask = newMask;
        }
    }

    //Utils
    private PorterDuffXfermode getModeFromInteger(int index) {
        PorterDuff.Mode mode = intToMode(index);
        log("Mode is " + mode.toString());
        return new PorterDuffXfermode(mode);
    }

    public static PorterDuff.Mode intToMode(int val) {
        switch (val) {
            default:
            case  0: return PorterDuff.Mode.CLEAR;
            case  1: return PorterDuff.Mode.SRC;
            case  2: return PorterDuff.Mode.DST;
            case  3: return PorterDuff.Mode.SRC_OVER;
            case  4: return PorterDuff.Mode.DST_OVER;
            case  5: return PorterDuff.Mode.SRC_IN;
            case  6: return PorterDuff.Mode.DST_IN;
            case  7: return PorterDuff.Mode.SRC_OUT;
            case  8: return PorterDuff.Mode.DST_OUT;
            case  9: return PorterDuff.Mode.SRC_ATOP;
            case 10: return PorterDuff.Mode.DST_ATOP;
            case 11: return PorterDuff.Mode.XOR;
            case 12: return PorterDuff.Mode.ADD;
            case 13: return PorterDuff.Mode.MULTIPLY;
            case 14: return PorterDuff.Mode.SCREEN;
            case 15: return PorterDuff.Mode.OVERLAY;
            case 16: return PorterDuff.Mode.DARKEN;
            case 17: return PorterDuff.Mode.LIGHTEN;
        }
    }
}
