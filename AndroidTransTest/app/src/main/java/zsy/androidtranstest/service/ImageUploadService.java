package zsy.androidtranstest.service;

/**
 * Created by zsy on 16/5/22.
 */

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import zsy.androidtranstest.utils.MD5;

/** * * 实现文件上传的工具类
 *
 */
public class ImageUploadService {
    private static final String TAG = "uploadFile";
    private static final int TIME_OUT = 100*1000; //超时时间
    private static final String CHARSET = "utf-8"; //设置编码
    public static final String SUCCESS="1";
    public static final String FAILURE="0";
    public static String srcPath;
    /** * android上传文件到服务器
     * @param file 需要上传的文件
     * @param RequestURL 请求的rul
     * @return 返回响应的内容
     */
    public static String uploadFile(File file,String RequestURL) {
        String BOUNDARY = UUID.randomUUID().toString(); //边界标识 随机生成
        String PREFIX = "--" , LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data"; //内容类型
        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true); //允许输入流
            conn.setDoOutput(true); //允许输出流
            conn.setUseCaches(false); //不允许使用缓存
            conn.setRequestMethod("POST"); //请求方式
            conn.setRequestProperty("Charset", CHARSET);
            //设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            if(file!=null) {
                /** * 当文件不为空，把文件包装并且上传 */
                OutputStream outputSteam=conn.getOutputStream();
                DataOutputStream dos = new DataOutputStream(outputSteam);
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY); sb.append(LINE_END);
                /**
                 * 这里重点注意：
                 * name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的 比如:abc.png
                 */
                sb.append("Content-Disposition: form-data; name=\"img\"; filename=\""+file.getName()+"\""+LINE_END);
                sb.append("Content-Type: application/octet-stream; charset="+CHARSET+LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while((len=is.read(bytes))!=-1)
                {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX+BOUNDARY+PREFIX+LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();
                /**
                 * 获取响应码 200=成功
                 * 当响应成功，获取响应的流
                 */
                int res = conn.getResponseCode();
                Log.e(TAG, "response code:" + res);
                if(res==200)
                {
                    return SUCCESS;
                }
            }
        } catch (MalformedURLException e)
        { e.printStackTrace(); }
        catch (IOException e)
        { e.printStackTrace(); }
        return FAILURE;
    }

    /**
     * android上传文件到服务器
     *
     * @param fileList       需要上传的文件
     * @param RequestURL 请求的rul
     * @return 返回响应的内容
     */
    public static String uploadFiles(List<File> fileList, String RequestURL, Map<String, String> param) {
        String result = null;
        String BOUNDARY = UUID.randomUUID().toString();  //边界标识   随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data";   //内容类型
        // 显示进度框
//      showProgressDialog();
        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true);  //允许输入流
            conn.setDoOutput(true); //允许输出流
            conn.setUseCaches(false);  //不允许使用缓存
            conn.setRequestMethod("POST");  //请求方式
            conn.setRequestProperty("Charset", CHARSET);  //设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);

            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

            StringBuffer sb;
            String params = "";
            if (param != null && param.size() > 0) {
                Iterator<String> it = param.keySet().iterator();
                while (it.hasNext()) {
//                            sb = null;
                    sb = new StringBuffer();
                    String key = it.next();
                    String value = param.get(key);
                    sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
                    sb.append("Content-Disposition: form-data; name=\"").append(key).append("\"").append(LINE_END).append(LINE_END);
                    sb.append(value).append(LINE_END);
                    params = sb.toString();
//                        Log.i(TAG, key + "=" + params + "##");
                    dos.write(params.getBytes());
//                          dos.flush();
                }
            }
            sb = new StringBuffer();
            sb.append(PREFIX);
            sb.append(BOUNDARY);
//            sb.append(LINE_END);
            dos.write(sb.toString().getBytes());

            for(int i = 0;i < fileList.size();++i){
                dos.write(LINE_END.getBytes());

                sb = new StringBuffer();
                /**
                 * 这里重点注意：
                 * name里面的值为服务器端需要key   只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的   比如:abc.png
                 */
                sb.append("Content-Disposition: form-data; name=\"upfile\";filename=\"" + fileList.get(i).getName() + "\"" + LINE_END);
                sb.append("Content-Type: image/pjpeg; charset=" + CHARSET + LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(fileList.get(i));
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY).getBytes();
                dos.write(end_data);
                dos.flush();
            }
            dos.write(PREFIX.getBytes());
            dos.write(LINE_END.getBytes());
            dos.flush();

            int res = conn.getResponseCode();
            Log.v("conn.getResponseCode()", "res=========" + res);
            if (res == 200) {
                InputStream input = conn.getInputStream();
                StringBuffer sb1 = new StringBuffer();
                int ss;
                while ((ss = input.read()) != -1) {
                    sb1.append((char) ss);
                }
                final String path = sb1.toString();
                result = path;
//                Log.v("result",""+path);
            }
            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static Uri getImageFromSever(String path, File cacheDir) throws Exception{
        File localFile = new File(cacheDir, MD5.getMD5(path)+path.substring(path.lastIndexOf(".")));
        if(localFile.exists()){
            return Uri.fromFile(localFile);
        }else{
            HttpURLConnection conn = (HttpURLConnection) new URL(path).getContent();
            conn.setConnectTimeout(10000);
            conn.setRequestMethod("GET");
            int code = conn.getResponseCode();
            if(code == 200){
                FileOutputStream os = new FileOutputStream(localFile);
                InputStream inputStream = conn.getInputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while( (len = inputStream.read(buffer)) != -1){
                    os.write(buffer, 0, len);
                }
                inputStream.close();
                os.close();
                return Uri.fromFile(localFile);
            }
        }

        return null;
    }


    public static void asyncImageLoad(ImageView imageView,String path,File cache,ContentResolver crr) {
        AsyncImageTask asyncImageTask = new AsyncImageTask(imageView,cache,crr);
        asyncImageTask.execute(path);
//        return srcPath;
    }
    /**
     * 使用AsyncTask异步加载图片
     *
     * @author Administrator
     *
     */
    public static class AsyncImageTask extends AsyncTask<String, Integer, Uri> {
        private ImageView imageView;
        private File cache;
        private ContentResolver crr;

        public AsyncImageTask(ImageView imageView,File cache, ContentResolver crr) {

            this.imageView = imageView;
            this.cache = cache;
            this.crr = crr;
        }

        protected Uri doInBackground(String... params) {// 子线程中执行的
//            Log.v("params",""+params[0]);
            try {
                return ImageUploadService.getImage(params[0], cache);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Uri result) {// 运行在主线程
            if (result != null) {
//                imageView.setImageURI(result);
//                srcPath = result.getPath();
//                this.imageView
                Log.v("onPostExecute",""+srcPath);

                Uri uri = Uri.fromFile(new File(result.getPath()));
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.crr, uri);
//                Log.v("bitmap in", "" + (bitmap.getHeight()));
                }catch (IOException e){
                    e.printStackTrace();
                }
//                bitmap = ThumbnailUtils.extractThumbnail(bitmap, 1600, 600);
//            Log.v("bitmap out", "" + (bitmap.getHeight()));
                this.imageView.setImageBitmap(bitmap);
            }
        }
    }

    /**
     * 获取网络图片,如果图片存在于缓存中，就返回该图片，否则从网络中加载该图片并缓存起来
     * @param path 图片路径
     * @return
     */
    public static Uri getImage(String path, File cacheDir) throws Exception{// path -> MD5 ->32字符串.jpg
        File localFile = new File(cacheDir, MD5.getMD5(path)+ path.substring(path.lastIndexOf(".")));
        if(localFile.exists()){
            return Uri.fromFile(localFile);
        }else{
            HttpURLConnection conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            if(conn.getResponseCode() == 200){
                FileOutputStream outStream = new FileOutputStream(localFile);
                InputStream inputStream = conn.getInputStream();
                byte[] buffer = new byte[4096];
                int len = 0;
                while( (len = inputStream.read(buffer)) != -1){
                    outStream.write(buffer, 0, len);
                }
                inputStream.close();
                outStream.close();
                return Uri.fromFile(localFile);
            }
        }
        return null;
    }

}