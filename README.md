## Maskable Layout


### Overview
=======================

The **Maskable Layout** is a simple **framelayout** that allows you to easily **mask** views and viewgroups. 
You can also execute other porterduffxfermodes. 
Simple **Drawables** are accepted, as well as **AnimationDrawables** !

### Example
=======================
![](/Screencast.gif)
### Usage
=======================
This example masks his child element (Imageview) with the mask "animation_mask" and sets the porterDuffXferMode to DST_IN

```xml
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

Or use JitPack: https://jitpack.io/#christophesmet/android_maskable_layout

```
repositories {
    maven { url 'https://jitpack.io' }
}
    implementation 'com.github.christophesmet:android_maskable_layout:v1.3.1'
```

License
=======

    Copyright 2015 Christophe Smet.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
