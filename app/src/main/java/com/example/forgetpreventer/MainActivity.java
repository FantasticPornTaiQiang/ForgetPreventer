package com.example.forgetpreventer;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forgetpreventer.Utils.FileSql;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static int SORT_STATE = 1;//1为默认(按时间)排序，2为按标题排序

    private DrawerLayout drawerLayoutMenu;
    private TextView addFileButton;
    private RecyclerView recyclerView;
    private List<FileSql> fileList;
    private ListView listView;
    private List<String> title = new ArrayList<>();
    private ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayoutMenu = (DrawerLayout)findViewById(R.id.drawer_menu);
        MyClickListener myClickListener = new MyClickListener();
        addFileButton = (TextView)findViewById(R.id.add_file_button);
        addFileButton.setOnClickListener(myClickListener);
        listView = findViewById(R.id.fileListView);
        adapter = new ArrayAdapter<String>(this, R.layout.file_item, title);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra("id", fileList.get(i).getId());
                intent.putExtra("content", fileList.get(i).getContent());
                startActivity(intent);
            }
        });

       // initSql();



        //关闭事件
        drawerLayoutMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayoutMenu.closeDrawer(Gravity.LEFT);
            }
        });
        //侧滑监听
        drawerLayoutMenu.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }


        });

    }

    private void initSql(){
        if (SORT_STATE == 1)//按照时间排序(默认)
        {
            fileList = new ArrayList<>();
            fileList = LitePal.where("istop = ?" , "1" )
                    .order("update_time")
                    .find(FileSql.class);
            List<FileSql> editSqlList = LitePal.where("istop = ?" , "0")
                    .order("create_time")
                    .find(FileSql.class);

            for (FileSql ee : editSqlList)
            {
                fileList.add(ee);
            }
        } else if (SORT_STATE == 2) {
            fileList = new ArrayList<>() ;
            fileList = LitePal.where("istop = ?" , "1" )
                    .order("title  collate localized  asc")//用于支持中文排序 collate localized  asc
                    .find(FileSql.class) ;
            List<FileSql> editSqlList = LitePal.where("istop = ?" , "0")
                    .order("title collate localized  asc ")
                    .find(FileSql.class);

            for (FileSql ee : editSqlList)
            {
                fileList.add(ee) ;
            }
        }
        for (FileSql sql : fileList){
            title.add(""+sql.getId());
        }
        adapter.notify();

    }

    public class MyClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.add_file_button:
                    Intent intent = new Intent(MainActivity.this , EditActivity.class) ;
                    startActivity(intent);
                    break;

                default:
            }
        }
    }




}
