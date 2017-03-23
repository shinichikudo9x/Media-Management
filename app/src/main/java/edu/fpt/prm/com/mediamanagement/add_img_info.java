package edu.fpt.prm.com.mediamanagement;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import tools.AlbumTool;


public class add_img_info extends AppCompatActivity {

    Button btn_save;
    EditText txt_title;
    String title;
    int id;
    AlbumTool tool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_img_info);
        //find_view_by_id
        get();
        Bundle bundle = getIntent().getExtras();
        id = bundle.getInt("id");
        title = bundle.getString("title");

        txt_title.setText(title);


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title = txt_title.getText().toString();

                tool.setMetadata(getApplicationContext(),id,title);
                finish();

            }
        });
    }

    protected void get(){
        btn_save = (Button) findViewById(R.id.ainf_btn_save);
        txt_title = (EditText) findViewById(R.id.ainf_title);
    }
}
