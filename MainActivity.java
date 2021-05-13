package com.example.chatbotleedaun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private ArrayList<ChatData> dataList;
    private MyAdapter itemAdapter;

    DrawerLayout drawerLayout;
    EditText EditText_chat;
    Button Button_send,Button_Menu1;
    RecyclerView recyclerView;
    ImageView iv_Menu;
    NavigationView navigationView;

    private final int REQUEST_VALUE = 1;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        StringBuilder sb = new StringBuilder();
        if (requestCode == REQUEST_VALUE) {
            sb.append("오늘의 학식 ʕ ・ Д ・ ʔ\n");
            if (resultCode == RESULT_OK) {
                sb.append(data.getStringExtra("result")); }
            else {  sb.append("불러오기 실패!"); }
        }
        //받은 데이터 채팅에 집어넣기
        askdataPrint(sb.toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer);
        Button_send = findViewById(R.id.Button_send);
        EditText_chat = findViewById(R.id.EditText_chat);
        iv_Menu = findViewById(R.id.iv_menu);
        navigationView = findViewById(R.id.navigation);
        Button_Menu1 = findViewById(R.id.action_menu1);
        navigationView.setNavigationItemSelectedListener(this);

        this.initializeData();
        itemAdapter = new MyAdapter(dataList);
        //Adapt,ChatData를 이용하여 채팅 내용 뽑기

        recyclerView = findViewById(R.id.recyclerview);

        LinearLayoutManager manager =
                new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(manager); //LayoutManager 등록
        recyclerView.setAdapter(itemAdapter); // Adapter 등록
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                recyclerView.scrollToPosition(dataList.size()-1);
            }
        });

        iv_Menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        AssetManager assetManager= getAssets();
        try {
            InputStream obj = assetManager.open("json/ask.json");
            InputStreamReader jObject = new InputStreamReader(obj);
            BufferedReader reader = new BufferedReader(jObject);

            StringBuffer buffer = new StringBuffer();
            String line1 = reader.readLine();
            while(line1!=null ){
                buffer.append(line1+"\n");
                line1 = reader.readLine();
            }
            String jsonData = buffer.toString();
            JSONObject jsonObject = new JSONObject(jsonData);

            Button_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String msg = EditText_chat.getText().toString();
                    //입력받은 값을 우측에 출력후 좌측에 알맞은 답변 생성
                    if((msg.getBytes().length<=0))  {
                        Toast.makeText(getApplicationContext(),"내용을 입력하세요",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        dataList.add(new ChatData(msg, null, Code.ViewType.RIGHT_CONTENT));
                        //공백제거, 소문자 -> 대문자
                        String sMsg = msg.replace(" ","").trim().toUpperCase();
                        itemAdapter.notifyDataSetChanged();
                        EditText_chat.setText("");
                        try {
                            if(sMsg.contains("학식")){
                                IntentFile();
                            }
                            else {
                                String value = jsonObject.getString(sMsg);
                                askdataPrint(value);
                            }
                        } catch (JSONException e) {
                            dataList.add(new ChatData("오타가 있거나 내가 배우지못한 단어인거같아 ! 메뉴정보를 확인하고 다시 얘기해줘!", null, Code.ViewType.LEFT_CONTENT));
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void askdataPrint(String askdata){
        dataList.add(new ChatData(askdata, null, Code.ViewType.LEFT_CONTENT));
        recyclerView.scrollToPosition(dataList.size()-1);
    }

    private void IntentFile(){
        Intent intent = new Intent(getApplicationContext(),Crawl.class);
        startActivityForResult(intent,REQUEST_VALUE);
    }
    private void initializeData() {
        dataList = new ArrayList<>();
        //LEFT 출력
        dataList.add(new ChatData("안녕! 원하는 질문이 있다면 언제든 얘기해줘\n ex)수강신청,도서관,휴학,컴퓨터소프트웨어과,자동차관,학과일정,대의원회,학식",null,Code.ViewType.LEFT_CONTENT));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_menu1:
                Intent intentMenu1 = new Intent(MainActivity.this, Menu1.class);
                startActivity(intentMenu1);
                break;
            case R.id.action_menu2:
                Intent intentMenu2 = new Intent(MainActivity.this, Menu2.class);
                startActivity(intentMenu2);
                break;
            case R.id.action_menu3:
                Intent intentMenu3 = new Intent(MainActivity.this, Menu3.class);
                startActivity(intentMenu3);
                break;
            case R.id.action_menu4:
                Intent intentMenu4 = new Intent(MainActivity.this, Menu4.class);
                startActivity(intentMenu4);
                break;
            case R.id.action_menu5:
                Intent intentMenu5 = new Intent(MainActivity.this, Menu5.class);
                startActivity(intentMenu5);
                break;
            case R.id.action_menu6:
                Intent intentMenu6 = new Intent(MainActivity.this, Menu6.class);
                startActivity(intentMenu6);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}