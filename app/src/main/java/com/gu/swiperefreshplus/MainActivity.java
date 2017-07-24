package com.gu.swiperefreshplus;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.gu.swiperefreshplus.extention.RefreshViewLayout;

public class MainActivity extends AppCompatActivity {
    private ViewPager mContent;
    private TabLayout mTabLayout;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContent= (ViewPager) findViewById(R.id.vp_content);
        mTabLayout=(TabLayout) findViewById(R.id.tab_layout);
        mToolbar=(Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        mContent.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        mTabLayout.setupWithViewPager(mContent);
    }

}
