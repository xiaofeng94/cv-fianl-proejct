package zsy.androidtranstest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.util.Calendar;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageGaussianBlurFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSepiaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSketchFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageToonFilter;
import zsy.androidtranstest.service.ImageUploadService;
import zsy.androidtranstest.utils.MD5;

public class ImageResultActivity extends AppCompatActivity {

    private ImageButton ibBack;
    private Button btDrop;
    private Button btSave;
    private ImageView ivResult;
    private ImageView ivOriginBackUp;
    private String resultPath;
    private File cache;
    private MyApplication myapp;
//    private Bitmap oriImage = null;
    private Bitmap currImage = null;

    private ImageButton[] btns = new ImageButton[6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_result);
        myapp = (MyApplication)getApplication();
        cache = new File(Environment.getExternalStorageDirectory(), "cache");

        Bundle bundle = this.getIntent().getExtras();
        resultPath = bundle.getString("resultPath");

        ivOriginBackUp = (ImageView)findViewById(R.id.iv_result_origin);
        ivResult = (ImageView)findViewById(R.id.iv_result);
        ImageUploadService.asyncImageLoad(ivResult,resultPath,cache,this.getContentResolver());

        ibBack = (ImageButton)findViewById(R.id.ib_back);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageResultActivity.this.finish();
            }
        });
        btDrop = (Button)findViewById(R.id.bt_drop);
        btDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageResultActivity.this.finish();
            }
        });

        btSave = (Button)findViewById(R.id.bt_save);
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ivResult.buildDrawingCache();
//                Bitmap bitmap=ivResult.getDrawingCache();
                if(currImage == null){
                    ivResult.buildDrawingCache();
//                    oriImage = ivResult.getDrawingCache();
                    currImage = ivResult.getDrawingCache();
                    ivOriginBackUp.setImageBitmap(currImage);
                }
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                currImage.compress(Bitmap.CompressFormat.JPEG , 100 , stream);

                byte[] byteArray = stream.toByteArray();
//                File dir=new File(Environment.getExternalStorageDirectory ().getAbsolutePath()+"/picture" );
                File dir=new File(Environment.getExternalStorageDirectory ().getAbsolutePath()+"/"+myapp.picSavePath );
                if(!dir.isFile()){
                    dir.mkdir();
//                    Log.v("mkdir","yes!");
                }

                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                int minute = c.get(Calendar.MINUTE);
                String imageName = MD5.getMD5("" + year + month + day+minute);
                File file=new File(dir,imageName + ".jpg" );
                try {
                    FileOutputStream fos=new FileOutputStream(file);
                    fos.write(byteArray, 0, byteArray.length);
                    fos.flush();

//                    Log.v("write file", "yes!");
                    Toast.makeText(ImageResultActivity.this,"save to:"+dir.getAbsolutePath()+"/"+imageName+".jpg",Toast.LENGTH_SHORT).show();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        initFilter();
    }

    private void initFilter(){
        btns[0] = (ImageButton)findViewById(R.id.ib_origin_image);
        btns[1] = (ImageButton)findViewById(R.id.ib_sepia);
        btns[2] = (ImageButton)findViewById(R.id.ib_gray);
        btns[3] = (ImageButton)findViewById(R.id.ib_blur);
        btns[4] = (ImageButton)findViewById(R.id.ib_cartton);
        btns[5] = (ImageButton)findViewById(R.id.ib_sketch);

        btns[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currImage == null){
                    ivResult.buildDrawingCache();
//                    oriImage = ivResult.getDrawingCache();
                    currImage = ivResult.getDrawingCache();
                    ivOriginBackUp.setImageBitmap(currImage);
                }
                ivOriginBackUp.buildDrawingCache();
                currImage = ivOriginBackUp.getDrawingCache();
                ivResult.setImageBitmap(currImage);
            }
        });

        btns[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currImage == null){
                    ivResult.buildDrawingCache();
//                    oriImage = ivResult.getDrawingCache();
                    currImage = ivResult.getDrawingCache();
                    ivOriginBackUp.setImageBitmap(currImage);
                }
                GPUImage gpuImage = new GPUImage(ImageResultActivity.this);
                gpuImage.setImage(currImage);
//                gpuImage.setFilter(new GPUImageThresholdEdgeDetection());
                gpuImage.setFilter(new GPUImageSepiaFilter());
                currImage = gpuImage.getBitmapWithFilterApplied();

                // 在ImageView中显示处理后的图像
                ivResult.setImageBitmap(currImage);
            }
        });

        btns[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currImage == null){
                    ivResult.buildDrawingCache();
//                    oriImage = ivResult.getDrawingCache();
                    currImage = ivResult.getDrawingCache();
                    ivOriginBackUp.setImageBitmap(currImage);
                }
                GPUImage gpuImage = new GPUImage(ImageResultActivity.this);
                gpuImage.setImage(currImage);
//                gpuImage.setFilter(new GPUImageThresholdEdgeDetection());
                gpuImage.setFilter(new GPUImageGrayscaleFilter());
                currImage = gpuImage.getBitmapWithFilterApplied();

                // 在ImageView中显示处理后的图像
                ivResult.setImageBitmap(currImage);
            }
        });

        btns[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currImage == null){
                    ivResult.buildDrawingCache();
//                    oriImage = ivResult.getDrawingCache();
                    currImage = ivResult.getDrawingCache();
                    ivOriginBackUp.setImageBitmap(currImage);
                }
                GPUImage gpuImage = new GPUImage(ImageResultActivity.this);
                gpuImage.setImage(currImage);
//                gpuImage.setFilter(new GPUImageThresholdEdgeDetection());
                gpuImage.setFilter(new GPUImageGaussianBlurFilter());
                currImage = gpuImage.getBitmapWithFilterApplied();

                // 在ImageView中显示处理后的图像
                ivResult.setImageBitmap(currImage);
            }
        });

        btns[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currImage == null){
                    ivResult.buildDrawingCache();
//                    oriImage = ivResult.getDrawingCache();
                    currImage = ivResult.getDrawingCache();
                    ivOriginBackUp.setImageBitmap(currImage);
                }
                GPUImage gpuImage = new GPUImage(ImageResultActivity.this);
                gpuImage.setImage(currImage);
//                gpuImage.setFilter(new GPUImageThresholdEdgeDetection());
                gpuImage.setFilter(new GPUImageToonFilter());
                currImage = gpuImage.getBitmapWithFilterApplied();

                // 在ImageView中显示处理后的图像
                ivResult.setImageBitmap(currImage);
            }
        });

        btns[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currImage == null){
                    ivResult.buildDrawingCache();
//                    oriImage = ivResult.getDrawingCache();
                    currImage = ivResult.getDrawingCache();
                    ivOriginBackUp.setImageBitmap(currImage);
                }
                GPUImage gpuImage = new GPUImage(ImageResultActivity.this);
                gpuImage.setImage(currImage);
//                gpuImage.setFilter(new GPUImageThresholdEdgeDetection());
                gpuImage.setFilter(new GPUImageSketchFilter());
                currImage = gpuImage.getBitmapWithFilterApplied();

                // 在ImageView中显示处理后的图像
                ivResult.setImageBitmap(currImage);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_result, menu);
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
