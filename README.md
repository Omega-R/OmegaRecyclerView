[![](https://jitpack.io/v/Omega-R/OmegaRecyclerView.svg)](https://jitpack.io/#Omega-R/OmegaRecyclerView)
[![GitHub license](https://img.shields.io/github/license/dcendents/android-maven-gradle-plugin.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

# OmegaRecyclerView
Custom RecyclerView allow you add divider

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

# License
```
Copyright 2017 Omega-R

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
