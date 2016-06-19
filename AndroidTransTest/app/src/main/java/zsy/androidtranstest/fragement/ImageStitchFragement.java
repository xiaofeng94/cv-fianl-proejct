package zsy.androidtranstest.fragement;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.BaseSwipListAdapter;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import zsy.androidtranstest.ImageResultActivity;
import zsy.androidtranstest.MyApplication;
import zsy.androidtranstest.R;
import zsy.androidtranstest.service.ContactService;
import zsy.androidtranstest.service.ImageUploadService;

/**
 * Created by zsy on 16/6/16.
 */
//new fragement for image stitching
public class ImageStitchFragement extends Fragment {

    private static final int TIME_OUT = 10 * 1000;   //超时时间
    private static final String CHARSET = "utf-8"; //设置编码

    private SwipeMenuListView sMListView;
    private List<String> paths;
    private MyAdapter mAdapter;
    private Button addImage;
    private Button submit;
    private MyApplication myApp;
    private String resultPath; //path of stitching result

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myApp = (MyApplication)getActivity().getApplication();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragement_image_stitch, container, false);

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
//                // create "open" item
//                SwipeMenuItem openItem = new SwipeMenuItem(
//                        getActivity());
//                // set item background
//                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
//                        0xCE)));
//                // set item width
//                openItem.setWidth(dp2px(90));
//                // set item title
//                openItem.setTitle("Open");
//                // set item title fontsize
//                openItem.setTitleSize(18);
                // set item title font color
//                openItem.setTitleColor(Color.WHITE);
//                // add to menu
//                menu.addMenuItem(openItem);

//                 create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getActivity());
                // set item background
                deleteItem.setBackground(R.color.grey);
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.tag_del);

//                deleteItem.setTitle("Delete");
////                // set item title fontsize
//                deleteItem.setTitleSize(18);
//                deleteItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        paths = new ArrayList<String>();
//        paths.add("test");
//        paths.add("test");
//        paths.add("test");
//        paths.add("test");


        mAdapter = new MyAdapter();
        sMListView = (SwipeMenuListView) view.findViewById(R.id.lv_iamges_stitch);

        sMListView.setMenuCreator(creator);
        sMListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        paths.remove(position);
                        mAdapter.notifyDataSetChanged();
                        // delete
                        break;
                    case 1:
                        // other
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
        sMListView.setAdapter(mAdapter);

        addImage = (Button) view.findViewById(R.id.add_stitch);
        submit = (Button) view.findViewById(R.id.submit_stitch);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent local = new Intent();
                local.setType("image/*");
                local.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(local, 2);
//                paths.add("test");


            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "submit:" + paths.size(), Toast.LENGTH_SHORT).show();
//                Log.v("imageViews", mAdapter.imageViews.size();
                for (int i = 0; i < paths.size();++i){
//                    Log.v("last_path", paths.get(i) + "----------保存路径2");
                }
                resultPath = submitUploadFile();
            }
        });

        return view;
    }

    /**
     * dp2px
     */
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    class MyAdapter extends BaseSwipListAdapter {

//        public List<ImageView> imageViews;
//
//        public MyAdapter() {
//            super();
//            imageViews = new ArrayList<>();
//        }

        @Override
        public int getCount() {
            return paths.size();
        }

        @Override
        public String getItem(int position) {
            return paths.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.lv_item_stitch, null);
            }

            ImageView image = (ImageView) convertView.findViewById(R.id.iv_imag_stitch);

//            Uri uri = Uri.parse(paths.get(position)); //for setImageUri

//            image.setImageURI(uri);

            Uri uri = Uri.fromFile(new File(paths.get(position)));
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
//                Log.v("bitmap in", "" + (bitmap.getHeight()));
            }catch (IOException e){
                e.printStackTrace();
            }

            bitmap = ThumbnailUtils.extractThumbnail(bitmap, 100, 100);
//            Log.v("bitmap out", "" + (bitmap.getHeight()));
            image.setImageBitmap(bitmap);
            return convertView;
        }


        @Override
        public boolean getSwipEnableByPosition(int position) {
//            if(position % 2 == 0){
//                return false;
//            }
            return true;
        }

        @Override
        public void notifyDataSetChanged (){
            super.notifyDataSetChanged();
//            Log.v("imageViews", mAdapter.imageViews.size() + "----------xxx2");
        }
    }

    @Override
    public void onResume() {
        //...更新View
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 2:
                    Uri uri = data.getData();
                    ContentResolver cr = getActivity().getContentResolver();
                    Cursor c = cr.query(uri, null, null, null, null);
                    c.moveToFirst();
                    //这是获取的图片保存在sdcard中的位置
                    paths.add(c.getString(c.getColumnIndex("_data")));
//                    Log.v("last_path", paths.get(paths.size() - 1) + "----------保存路径2");
                    mAdapter.notifyDataSetChanged();

//                    mAdapter.imageViews.get(mAdapter.imageViews.size()-1).setImageURI(uri);
//                    Log.v("imageViews", mAdapter.imageViews.size() + "----------保存路径2");
                    break;
                default:
                    break;
//            }
            }
        }
    }

    private String submitUploadFile() {

        final String RequestURL = "http://" + myApp.serverIP + ":8080/AndroidServerTest/"+myApp.stitchServer;
        final Map<String, String> params = new HashMap<String, String>();
        final List<File> updateFileList = new ArrayList<>();

//        final File file = new File(paths.get(0));
//
//            if (file == null || (!file.exists())) {
//                return;
//            }
//
////            Log.i(TAG, "请求的URL=" + RequestURL);
////            Log.i(TAG, "请求的fileName=" + file.getName());
//            final Map<String, String> params = new HashMap<String, String>();
////            params.put("user_id", loginKey);
////            params.put("file_type", "1");
////            params.put("content", img_content.getText().toString());
////            showProgressDialog();
//            new Thread(new Runnable() { //开启线程上传文件
//                @Override
//                public void run() {
//                    uploadFile(file, RequestURL, params);
//                }
//            }).start();


        for(int i = 0;i < paths.size();++i) {
            File file = new File(paths.get(i));
            if (file == null || (!file.exists())) {
                continue;
            }
            updateFileList.add(file);
        }
        new Thread(new Runnable() { //开启线程上传文件
            @Override
            public void run() {
                resultPath = ImageUploadService.uploadFiles(updateFileList, RequestURL, params);

                Log.v("resultPath",""+resultPath);

                if(resultPath != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("resultPath",resultPath);

                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    intent.setClass(ImageStitchFragement.this.getActivity(), ImageResultActivity.class);
                    startActivity(intent);
                }
            }
        }).start();
        return resultPath;
    }
}


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.action_left) {
//            mListView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
//            return true;
//        }
//        if (id == R.id.action_right) {
//            mListView.setSwipeDirection(SwipeMenuListView.DIRECTION_RIGHT);
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


