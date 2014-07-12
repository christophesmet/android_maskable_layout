package com.christophesmet.android.views;

import com.christophesmet.android.views.maskableframelayout.MaskableFrameLayout;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity {

    private MaskableFrameLayout mMaskableFrameLayout;
    private Button mBtnAnimate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMaskableFrameLayout = (MaskableFrameLayout) findViewById(R.id.frm_mask_animated);
        mBtnAnimate = (Button) findViewById(R.id.btn_animate);
        mBtnAnimate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animate();
            }
        });
    }

    private void animate() {
        Drawable drawable = mMaskableFrameLayout.getDrawableMask();
        if (drawable instanceof AnimationDrawable) {
            AnimationDrawable animDrawable = (AnimationDrawable) drawable;
            animDrawable.selectDrawable(0);
            animDrawable.stop();
            animDrawable.start();
        }
    }
}
