package zsy.androidtranstest;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import zsy.androidtranstest.fragement.ImageStitchFragement;
import zsy.androidtranstest.fragement.NewOldImageFragment;

public class MainFragementActivity extends AppCompatActivity {

    private FragmentManager manager;
    private Fragment[] fragments = new Fragment[2];
    private RadioButton[] btns = new RadioButton[3];
    private DrawerLayout drawerLayout;
    private LinearLayout rightDrawer;
    private EditText serverIPAddr;
    private MyApplication myApp;
//    private boolean openRDrawer;
    private Fragment currFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragement);
        myApp = (MyApplication)getApplication();
        drawerLayout = (DrawerLayout)findViewById(R.id.drawerlayout);
        rightDrawer = (LinearLayout)findViewById(R.id.right_drawer);
//        currFragmentIndx = 0;
//        openRDrawer = false;

        manager = getSupportFragmentManager();
        fragments[0] = new ImageStitchFragement();
        fragments[1] = new NewOldImageFragment();

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragement_container, fragments[0]);
        transaction.commit();
        currFragment = fragments[0];


        btns[0] = (RadioButton)findViewById(R.id.image_stitching);
        btns[1] = (RadioButton)findViewById(R.id.new_old_stitching);
        btns[2] = (RadioButton)findViewById(R.id.setting);


        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager)this.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(metrics);
        int scale = (int) metrics.density;
        //设置RadioButton图片的大小
        for (int i = 0;i < btns.length;i++) {
            Drawable[] drawables = btns[i].getCompoundDrawables();
            drawables[1].setBounds(0, 0,30*scale, 30*scale);
            btns[i].setCompoundDrawables(null, drawables[1], null, null);
        }

        btns[0].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
//                    FragmentTransaction transaction = manager.beginTransaction();
//                    transaction.replace(R.id.fragement_container, fragments[0]);
//                    transaction.commit();
                    switchContent(currFragment,fragments[0]);
                }
            }
        });
        btns[1].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
//                    FragmentTransaction transaction = manager.beginTransaction();
//                    transaction.replace(R.id.fragement_container,fragments[1]);
//                    transaction.commit();
                    switchContent(currFragment,fragments[1]);
                }
            }
        });
        btns[2].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
//                    FragmentTransaction transaction = manager.beginTransaction();
//                    transaction.replace(R.id.fragement_container,fragments[2]);
//                    transaction.commit();

                    drawerLayout.openDrawer(rightDrawer);

                }
            }
        });
        serverIPAddr = (EditText)findViewById(R.id.et_server_ip);
        serverIPAddr.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                Log.v("textChanged",""+s);
//                myApp.serverIP = s.toString();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                Log.v("afterTextChanged",""+s.toString());
                myApp.serverIP = s.toString();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_fragement, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void switchContent(Fragment from, Fragment to) {
        if (currFragment != to) {
            currFragment= to;
            FragmentTransaction transaction = manager.beginTransaction();
            if (!to.isAdded()) {    // 先判断是否被add过
                transaction.hide(from).add(R.id.fragement_container, to).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
            }
        }
    }
}
