package zsy.androidtranstest;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import zsy.androidtranstest.adapter.ContactAdapter;
import zsy.androidtranstest.domain.Contact;
import zsy.androidtranstest.service.ContactService;

public class MainActivity extends Activity {
    ListView listView;
    File cache; // 缓存文件

    private EditText ipAddr;
    private Button reConnect;
    private Button toUpdate;

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            listView.setAdapter(new ContactAdapter(MainActivity.this,
                    (List<Contact>) msg.obj, R.layout.listview_item, cache));
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
//        final String path = getResources().getString(R.string.contact_path);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) this.findViewById(R.id.listView);
        reConnect = (Button)this.findViewById(R.id.reConnect);
        ipAddr = (EditText)this.findViewById(R.id.ip_addr);
        toUpdate = (Button)this.findViewById(R.id.to_update);

        toUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,UpdateActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        reConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String path = "http://"+ipAddr.getText()+":8080/AndroidServerTest/list.xml";

                // 开一个线程来加载数据
                cache = new File(Environment.getExternalStorageDirectory(), "cache"); // 实例化缓存文件
                if (!cache.exists())
                    cache.mkdirs(); // 如果文件不存在，创建
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            List<Contact> data = ContactService.getContacts(path);
                            // 通过handler来发送消息
                            handler.sendMessage(handler.obtainMessage(22, data));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

    }

    @Override
    protected void onDestroy() {
        // 删除缓存
        for (File file : cache.listFiles()) {
            file.delete();
        }
        cache.delete();
        super.onDestroy();
    }

}
