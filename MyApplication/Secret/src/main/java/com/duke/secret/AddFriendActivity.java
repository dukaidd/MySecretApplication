package com.duke.secret;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.duke.adapters.AddFriendLvAdapter;
import com.duke.base.BaseActivity;
import com.duke.beans.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class AddFriendActivity extends BaseActivity implements SearchView.OnQueryTextListener, AdapterView.OnItemClickListener {

    private SearchView mSearchView;
    private ListView lv;
    private AddFriendLvAdapter adapter;
    private List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                onBackPressed();
            }
        });
        initView();
    }

    private void initView() {
        mSearchView = (SearchView) findViewById(R.id.search_view);
        //让searchView 展开 true是收缩
        mSearchView.setIconified(false);
        mSearchView.requestFocus();
        //设置searchinco 在搜索框中
        mSearchView.setIconifiedByDefault(true);
        //设置提示语
        mSearchView.setQueryHint("搜索用户名");
        mSearchView.setOnQueryTextListener(this);
        lv= (ListView) findViewById(R.id.search_listview);
        lv.setOnItemClickListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        BmobQuery<User> query1 = new BmobQuery<>();
        query1.addWhereContains("username", query);
        query1.setLimit(10);
        query1.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    users = list;
                    adapter = new AddFriendLvAdapter(AddFriendActivity.this, users);
                    lv.setAdapter(adapter);
                } else {
                    Log.e("duke", e.toString());
                }
            }
        });
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(AddFriendActivity.this,FriendMsgActivity.class);
        intent.putExtra("author",users.get(position).getUsername());
        startActivity(intent);
    }
}
