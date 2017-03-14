package edu.fpt.prm.com.mediamanagement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class HomeScreen extends AppCompatActivity {

    ListView listView;
    int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        ImageLoader loader = ImageLoader.getInstance();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        loader = ImageLoader.getInstance();
        loader.init(config);
        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        ArrayList<MediaEntry> list = ListAlbum.getAllListAlbum(this);
        listView = (ListView) findViewById(R.id.listDateAlbum);
        ListViewCustom adapter = new ListViewCustom(this,list,loader);
        listView.setAdapter(adapter);
        listView.setVerticalScrollbarPosition(position);
        listView.setDivider(null);
        final ArrayList<Integer> selectedItem = new ArrayList<>();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if(selectedItem.isEmpty()){
                    Intent intent = new Intent(getBaseContext(),ViewFile.class);
                    intent.putExtra("position",position);
                    startActivity(intent);
                }
                else{
                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.cbSelected);
                    checkBox.setChecked(true);
                    checkBox.setVisibility(View.VISIBLE);
                    selectedItem.add(position);
                    checkBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CheckBox box = (CheckBox) v;
                            if(!box.isChecked()){
                                box.setChecked(false);
                                box.setVisibility(View.GONE);
                                selectedItem.remove((Object)position);
                            }
                        }
                    });
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.cbSelected);
                checkBox.setChecked(true);
                checkBox.setVisibility(View.VISIBLE);
                selectedItem.add(position);
                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBox box = (CheckBox) v;
                        if(!box.isChecked()){
                            box.setChecked(false);
                            box.setVisibility(View.GONE);
                            selectedItem.remove((Object)position);
                        }
                    }
                });
                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        return false;
    }

}
