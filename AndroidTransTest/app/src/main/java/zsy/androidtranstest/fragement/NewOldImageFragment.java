package zsy.androidtranstest.fragement;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import zsy.androidtranstest.ImageResultActivity;
import zsy.androidtranstest.MyApplication;
import zsy.androidtranstest.R;
import zsy.androidtranstest.service.ImageUploadService;

/**
 * Created by zsy on 16/6/16.
 */
public class NewOldImageFragment extends Fragment {
    private ImageView oldImage;
    private ImageView newImage;
    private String oldImagePath;
    private String newImagePath;
    private Button submit;

    private String resultPath; //path of new-old images
    private MyApplication myApp;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myApp = (MyApplication)getActivity().getApplication();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_new_old, container, false);
        oldImage = (ImageView)view.findViewById(R.id.iv_old_image);
        newImage = (ImageView)view.findViewById(R.id.iv_new_image);
        submit = (Button)view.findViewById(R.id.bt_submit_old_new);

        oldImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //choose picture
                Intent local = new Intent();
                local.setType("image/*");
                local.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(local, 1);
            }
        });
        newImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //choose picture
                Intent local = new Intent();
                local.setType("image/*");
                local.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(local, 2);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), oldImagePath + " " + newImagePath, Toast.LENGTH_SHORT).show();
                submitUploadFile();


            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            ContentResolver cr = getActivity().getContentResolver();
            Cursor c = cr.query(uri, null, null, null, null);
            c.moveToFirst();
            switch (requestCode) {
                case 1:
                    //这是获取的图片保存在sdcard中的位置
                    oldImagePath = c.getString(c.getColumnIndex("_data"));
                    if(oldImagePath != null) {
                        Uri imageUri = Uri.fromFile(new File(oldImagePath));
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
//                Log.v("bitmap in", "" + (bitmap.getHeight()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        bitmap = ThumbnailUtils.extractThumbnail(bitmap, 200, 150);
//            Log.v("bitmap out", "" + (bitmap.getHeight()));
                        oldImage.setImageBitmap(bitmap);
                    }


                    break;
                case 2:
                    //这是获取的图片保存在sdcard中的位置
                    newImagePath = c.getString(c.getColumnIndex("_data"));

                    if(newImagePath != null) {
                        Uri imageUri = Uri.fromFile(new File(newImagePath));
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
//                Log.v("bitmap in", "" + (bitmap.getHeight()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        bitmap = ThumbnailUtils.extractThumbnail(bitmap, 200, 150);
//            Log.v("bitmap out", "" + (bitmap.getHeight()));
                        newImage.setImageBitmap(bitmap);
                    }

                    break;
                default:
                    break;
//            }
            }
        }
    }

    private void submitUploadFile() {

        final String RequestURL = "http://" + myApp.serverIP + ":8080/AndroidServerTest/"+myApp.newOldServer;
        final Map<String, String> params = new HashMap<String, String>();
        final List<File> updateFileList = new ArrayList<>();

        if(oldImagePath == null && newImagePath == null){
            return;
        }
        File oldFile = new File(oldImagePath);
        File newFile = new File(newImagePath);
        if(oldFile != null && newFile != null){
            updateFileList.add(oldFile);
            updateFileList.add(newFile);

            new Thread(new Runnable() { //开启线程上传文件
                @Override
                public void run() {
                    resultPath = ImageUploadService.uploadFiles(updateFileList, RequestURL, params);
                    Log.v("resultPath", "" + resultPath);

                    if(resultPath != null) {
                        Bundle bundle = new Bundle();
                        bundle.putString("resultPath",resultPath);

                        Intent intent = new Intent();
                        intent.putExtras(bundle);
                        intent.setClass(NewOldImageFragment.this.getActivity(), ImageResultActivity.class);
                        startActivity(intent);
                    }
                }
            }).start();
        }else{
            Toast.makeText(getActivity(),"fail to submit",Toast.LENGTH_SHORT).show();
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

}