[![](https://jitpack.io/v/Omega-R/OmegaRecyclerView.svg)](https://jitpack.io/#Omega-R/OmegaRecyclerView)
[![GitHub license](https://img.shields.io/github/license/dcendents/android-maven-gradle-plugin.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

# OmegaRecyclerView
Custom RecyclerView with additional functionality. Allow you add divider, itemSpace, emptyView, sticky header
and some other features

<p align="center">
    <img src="/images/recycler_view.gif?raw=true" width="300" height="533" />
</p>

# Installation
To get a Git project into your build:

**Step 1.** Add the JitPack repository to your build file
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
**Step 2.** Add the dependency
```
dependencies {
    compile 'com.github.Omega-R:OmegaRecyclerView:v1.1'
}
```

# Usage
Example of usage in xml layout
```
com.omega_r.libs.omegarecyclerview.OmegaRecyclerView
        android:id="@+id/custom_recycler_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:divider="1"
        android:dividerHeight="1dp"
        app:itemSpace="12dp"
        app:showDivider="middle"/>
```

## StickyHeader
<p align="center">
    <img src="/images/sticky_header.gif?raw=true" width="300" height="533" />
</p>

To add sticky header into project you need implements StickyHeaderAdapter in adapters class
```
public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHolder>
        implements StickyHeaderAdapter<TestAdapter.HeaderHolder>
```

Proper appearence of sticky header when using space item
```
stickyHeaderDecoration.setItemSpace(omegaRecyclerView.getItemSpace());
```

You can use findViewById without itemView
```
nameTextView = findViewById(R.id.text_contact_name);
messageButton = findViewById(R.id.button_message);
```

For array use BaseArrayAdapter
```
getItemCount()
getItem(position)
set(array)
add(array)
```

# License
```
The MIT License

Copyright 2017 Omega-R

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and 
associated documentation files (the "Software"), to deal in the Software without restriction, 
including without limitation the rights to use, copy, modify, merge, publish, distribute, 
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is 
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE 
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
```
