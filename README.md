[![](https://jitpack.io/v/Omega-R/OmegaRecyclerView.svg)](https://jitpack.io/#Omega-R/OmegaRecyclerView)
[![GitHub license](https://img.shields.io/github/license/mashape/apistatus.svg)](https://opensource.org/licenses/MIT)

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
    implementation 'com.github.Omega-R:OmegaRecyclerView:1.10.1@aar' // AndroidX
    // or
    // implementation 'com.github.Omega-R:OmegaRecyclerView:1.8.2@aar' // Android Support
}
```

# Usage
Example of usage in xml layout
```
<com.omega_r.libs.omegarecyclerview.OmegaRecyclerView
        android:id="@+id/custom_recycler_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:divider="#888"
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

## SwipeMenu
<p align="center">
    <img src="/images/swipe_menu_example.gif?raw=true" width="300" height="533" />
</p>

To add swipe menu into project you need to use SwipeViewHolder or create your own ViewHolder.
```
public class ViewHolder extends SwipeViewHolder {

    public ViewHolder(ViewGroup parent) {
            super(parent, 
            R.layout.item_swipe_content, 
            R.layout.item_left_swipe_menu, 
            R.layout.item_right_swipe_menu);
    }        
```

Also you can use constructor only with left menu, or only with right menu. 
```
    public ViewHolder(ViewGroup parent) {
            super(parent, 
            R.layout.item_swipe_content, 
            R.layout.item_left_swipe_menu, 
            SwipeViewHolder.NO_ID);
    }
```
Also you can use one layout for left menu and right menu. 
```
    public ViewHolder(ViewGroup parent) {
            super(parent, 
            R.layout.item_swipe_content, 
            R.layout.swipe_menu);
    }
```

You can use following methods for controlling swipe menu state:
```
public void setSwipeFractionListener(@Nullable SwipeFractionListener listener);
public void setSwipeListener(@Nullable SwipeSwitchListener listener);
public void smoothCloseMenu(int duration);
public void smoothCloseMenu();
public void smoothOpenBeginMenu();
public void smoothOpenEndMenu();
public void setSwipeEnable(boolean enable);
public boolean isSwipeEnable();
```

## Pagination
<p align="center">
    <img src="/images/pagination.gif?raw=true" width="300" height="533" />
</p>

To add pagination into project you just need to setPaginationCallback to OmegaRecyclerView.
```
mRecyclerView.setPaginationCallback(new OnPageRequestListener() {
            @Override
            public void onPageRequest(int page) {
                // You can load data inside this callback
            }

            @Override
            public int getPagePreventionForEnd() {
                return PREVENTION_VALUE; // PREVENTION_VALUE - for how many positions until the end you want to be informed
            }
        });
```
How to control pagination: 
```
mRecyclerView.showProgressPagination(); // show progress
mRecyclerView.hidePagination(); // hide pagination
mRecyclerView.showErrorPagination(); // show error
```

You have two ways to add your custom pagination layout and error layout. 
First way:
```
<com.omega_r.libs.omegarecyclerview.OmegaRecyclerView
        android:id="@+id/recyclerview"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:paginationLayout="@layout/item_progress"
        app:paginationErrorLayout="@layout/item_error_loading"/>
        
```
You can implement PaginationView in your Adapter class. 
Second:
```
public class RecyclerAdapter extends OmegaRecyclerView.Adapter<RecyclerView.ViewHolder> implements PaginationViewCreator {
....
@Nullable
    @Override
    public View createPaginationView(ViewGroup parent, LayoutInflater inflater) {
        return inflater.inflate(R.layout.item_progress, parent, false);
    }

    @Nullable
    @Override
    public View createPaginationErrorView(ViewGroup parent, LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.item_error_loading, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on error clicked....
            }
        });
        return view;
    }
```

## Sections (Header, Footer)
<p align="center">
    <img src="/images/sections_example.gif?raw=true" width="300" height="533" />
</p>

For usage just add you Views inside OmegaRecyclerView and add "app:layout_section" parameter.
```
<?xml version="1.0" encoding="utf-8"?>
<com.omega_r.libs.omegarecyclerview.OmegaRecyclerView 
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <TextView
        android:layout_width="match_parent"
        android:layout_height="100dp"
        android:text="Header"
        app:layout_section="header"/>

    <TextView
        android:layout_width="match_parent"
        android:layout_height="100dp"
        android:text="Footer"
        app:layout_section="footer"/>

</com.omega_r.libs.omegarecyclerview.OmegaRecyclerView>
```

For controll
```
OmegaRecyclerView.setHeadersVisibility(true);
OmegaRecyclerView.setFootersVisibility(false);
```

## ViewPager
<img src="/images/viewpager_scale.gif?raw=true" width="300" height="533" />    <img src="/images/viewpager_vertical.gif?raw=true" width="300" height="533" />

For usage just add OmegaPagerRecyclerView
```
<?xml version="1.0" encoding="utf-8"?>
    <com.omega_r.libs.omegarecyclerview.viewpager.OmegaPagerRecyclerView
        android:id="@+id/recyclerview"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical"

        // Supported features
        app:infinite="true"   // Infinite scroll
        app:pageSize="0.8" // set page size 80% of screen
        app:transitionTime="2100"/>
```

You could use your owner transformation.
```
        recyclerView.setItemTransformer(new ScaleTransformer.Builder()
                                                            .setMaxScale(1.1f)
                                                            .setMinScale(0.8f)
                                                            .build());
```

### [You could see our otransformations on wiki page.](https://github.com/Omega-R/OmegaRecyclerView/wiki/ViewPager)


## BaseListAdapter
Most common usage of RecyclerView is showing list of items. To simplify adapter creation just use BaseListAdapter:
```
// Adapter for List<String>
public class ListAdapter extends BaseListAdapter<String> {

    @Override
    protected ViewHolder provideViewHolder(ViewGroup parent) {
        return new SampleViewHolder(parent);
    }

    class SampleViewHolder extends ViewHolder {

        private TextView textView;

        // there should be layout setting and binding
        SampleViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_string);
            textView = findViewById(R.id.textview);
        }

        @Override
        protected void onBind(String item) {
            textView.setText(item);
        }
    }
}
```

Methods to change items:
 ```
 setItems(List<T> items)
 ```
 ```
 addItems(List\<T\> items)
 ```
Adapter will be automatically safely notified.

BaseListAdapter also have methods to click events handling:
```
setClickListener(@Nullable OnItemClickListener<T> clickListener)
```
```
setLongClickListener(@Nullable OnItemLongClickListener<T> longClickListener)
```
You can subscribe to this event on the fly - all view holders will be notified.

## Expandable
<img src="/images/exp_fade_single.gif?raw=true" width="250" height="400" />    <img src="/images/exp_dd_single.gif?raw=true" width="250" height="400" />    <img src="/images/exp_dd_multi.gif?raw=true" width="250" height="400" />

Adding OmegaExpandableRecyclerView to layout:
```
<com.omega_r.libs.omegarecyclerview.expandable_recycler_view.OmegaExpandableRecyclerView
        android:id="@+id/recyclerview"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:childAnimation="Dropdown"
        app:expandMode="single"/>
```

Create adapter like following:
```
public class ExpandableAdapter extends OmegaExpandableRecyclerView.Adapter<QuoteGlobalInfo, Quote> {

    @Override
    protected ExGroupViewHolder provideGroupViewHolder(@NonNull ViewGroup viewGroup) {
        return new ExGroupViewHolder(viewGroup);
    }

    @Override
    protected ExChildViewHolder provideChildViewHolder(@NonNull ViewGroup viewGroup) {
        return new ExChildViewHolder(viewGroup);
    }

    class ExGroupViewHolder extends GroupViewHolder {

        private TextView textView;

        ExGroupViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_exp_group);
            textView = findViewById(R.id.textview_group_name);
        }

        @Override
        protected void onExpand(GroupViewHolder viewHolder, int groupIndex) {
            // nothing
        }

        @Override
        protected void onCollapse(GroupViewHolder viewHolder, int groupIndex) {
            // nothing
        }

        @Override
        protected void onBind(QuoteGlobalInfo item) {
            textView.setText(item.getTitle());
        }
    }

    class ExChildViewHolder extends ChildViewHolder {

        private TextView textView;

        ExChildViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_exp_child);
            textView = findViewById(R.id.textview_child_content);
        }

        @Override
        protected void onBind(Quote item) {
            textView.setText(item.getQuote());
        }
    }
}
```

Use one of the following methods to set items for adapter or use adapter constructors:
```
public final void setItems(@NonNull List<ExpandableViewData<G, CH>> expandableViewData)
public final void setItems(ExpandableViewData<G, CH>... expandableViewData)
public final void setItemsAsGroupProviders(GroupProvider<G, CH>... groupProviders)
public final void setItemsAsGroupProviders(@NonNull List<GroupProvider<G, CH>> groupProviders)
```

Your view data (group and childs) should be wrapped with ```ExpandableViewData``` using one of the following ways:
```
ExpandableViewData(G group, List<CH> childs, @Nullable Integer stickyId) // constructor

static <G, CH> ExpandableViewData<G, CH> of(G group, @Nullable Integer stickyId, List<CH> childs)
static <G, CH> ExpandableViewData<G, CH> of(G group, @Nullable Integer stickyId, CH... childs)
```

Or it should implement interface 
```
public interface GroupProvider<G, CH> {
    G provideGroup();

    List<CH> provideChilds();

    @Nullable
    Integer provideStickyId();
}
```

It is available to update only child item using 
```
// Adapter
public void notifyChildChanged(CH child)
```

You can set expandMode and childAnimation both with xml attr and programmatically

*Available expandModes are single and multiple*

*Available childAnimations are Dropdown and Fade*

You can simply create your own animations by extending ```ExpandableItemAnimator``` class and then just set it to RecyclerView ```recyclerView.setItemAnimator```

<img src="/images/exp_bg.gif?raw=true" width="250" height="400" />

It is possible to emulate background expanding using "app:backgrounds" attribute.

Create LevelListDrawable in res/drawable folder

*expandable_backgrounds.xml*
```
<?xml version="1.0" encoding="utf-8"?>
<level-list xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:maxLevel="@integer/backgroundGroupCollapsed" android:drawable="@drawable/group_collapsed" />
    <item android:maxLevel="@integer/backgroundGroupExpanded" android:drawable="@drawable/group_expanded" />
    <item android:maxLevel="@integer/backgroundFirstChild" android:drawable="@drawable/child_bg" />
    <item android:maxLevel="@integer/backgroundLastChild" android:drawable="@drawable/child_last" />
    <item android:maxLevel="@integer/backgroundChild" android:drawable="@drawable/child_bg" />
</level-list>
```

And then just add ```app:backgrounds="@drawable/expandable_backgrounds"``` attribute to your OmegaExpandableRecyclerView

<img src="/images/exp_sticky_sup.gif?raw=true" width="250" height="400" />   <img src="/images/exp_sticky_group.gif?raw=true" width="250" height="400" />   <img src="/images/exp_sticky_all.gif?raw=true" width="250" height="400" />

OmegaExpandableRecyclerView have 2 ways of using Sticky Header feature: Default (that is described above in StickyHeader section) and StickyGroups. Last one is the way to make GroupViewHolders work as StickyHeaders.

To do that just add ```app:stickyGroups="true"``` attribute to your OmegaExpandableRecyclerView and be sure that you set items providing unique ```stickyId``` for each group.

And, of course, you CAN use both sticky behaviors at the same time


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
