## Maskable Layout
=======================

### Overview
=======================

The **Maskable Layout** is a simple **framelayout** that allow you to easily **mask** view and viewgroups. 
You can also execute other porterduffxfermodes. 
Simple **Drawables** are accepted, as wel as **AnimationDrawables** !

### Example
=======================
![](/Screencast.gif)
### Usage
=======================
This example masks his child element (Imageview) with the mask "animation_mask" and sets the porterDuffXferMode to DST_IN
```
 <com.christophesmet.android.views.maskableframelayout.MaskableFrameLayout
                        android:id="@+id/frm_mask_animated"
                        android:layout_width="100dp"
                        app:porterduffxfermode="DST_IN"
                        app:mask="@drawable/animation_mask"
                        android:layout_height="100dp">
                    <ImageView android:layout_width="match_parent"
                               android:layout_height="match_parent"
                               android:scaleType="centerCrop"
                               android:src="@drawable/unicorn"/>

                </com.christophesmet.android.views.maskableframelayout.MaskableFrameLayout>
```
### Building
=======================
Fork the repository and include the 'library' module and you are done :)
AAR File coming to soon to maven central near you ;)
