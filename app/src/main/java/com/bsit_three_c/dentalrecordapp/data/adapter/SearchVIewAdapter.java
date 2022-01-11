package com.bsit_three_c.dentalrecordapp.data.adapter;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import androidx.core.content.res.ResourcesCompat;

import com.bsit_three_c.dentalrecordapp.R;

public class SearchVIewAdapter implements SearchView.OnQueryTextListener, SearchView.OnCloseListener, MenuItem.OnActionExpandListener {

    private final Activity activity;
    private final MenuItem searchItem;
    private final SearchView searchView;
    private final ItemAdapter adapter;

    public SearchVIewAdapter(Activity activity, ItemAdapter adapter, MenuItem searchItem) {
        this.activity = activity;
        this.adapter = adapter;
        this.searchItem = searchItem;
        searchView = (SearchView) searchItem.getActionView();

        initializeViewUI();
    }

    public static SearchVIewAdapter newInstance(Activity context, ItemAdapter adapter, MenuItem searchItem) {
        return new SearchVIewAdapter(context, adapter, searchItem);
    }

    public SearchView getSearchView() {
        return searchView;
    }

    public MenuItem getSearchItem() {
        return searchItem;
    }

    public ItemAdapter getAdapter() {
        return adapter;
    }

    private void initializeViewUI() {
        searchView.setQueryHint("Search patients here");
        searchView.setBackground(ResourcesCompat.getDrawable(activity.getResources(), R.drawable.shape_field, activity.getTheme()));
        searchView.setBackgroundTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.tint_blue_green)));
    }

    public SearchVIewAdapter setSearchableInfo(SearchManager searchManager) {
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.getComponentName()));
        return this;
    }

    public void setListeners(SearchView.OnQueryTextListener queryTextListener, SearchView.OnCloseListener onCloseListener, MenuItem.OnActionExpandListener onActionExpandListener) {
        searchView.setOnQueryTextListener(queryTextListener);
        searchView.setOnCloseListener(onCloseListener);
        searchItem.setOnActionExpandListener(onActionExpandListener);
    }

    public SearchVIewAdapter setListeners() {
        setListeners(this, this, this);
        return this;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.i("onQueryTextSubmit", query);
        adapter.getFilter().filter(query);
        searchView.clearFocus();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.getFilter().filter(newText);
        Log.i("onQueryTextChange", newText);

        return false;
    }

    @Override
    public boolean onClose() {
        searchView.setQuery("", false);
        searchItem.collapseActionView();

        InputMethodManager inputManager = (InputMethodManager)
                activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        return false;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        searchView.setQuery("", false);
        return true;
    }
}
