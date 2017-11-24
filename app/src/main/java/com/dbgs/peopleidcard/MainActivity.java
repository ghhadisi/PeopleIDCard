package com.dbgs.peopleidcard;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 身份证竖着拍
 */
public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.

    Button btn_select_image;
    ImageView imageView;
    Button btn_discriminate;
    TextView tv_result;
    public static final int REQUEST_HEAD_IMAGE = 2000;

    private TessBaseAPI baseApi;
    private String language = "cn";
    private Bitmap template;
    private Bitmap fullImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_select_image = (Button) findViewById(R.id.btn_select_image);
         imageView = (ImageView) findViewById(R.id.imageView);
         btn_discriminate = (Button) findViewById(R.id.btn_discriminate);
        tv_result= (TextView) findViewById(R.id.tv_result);
        // Example of a call to a native method
        btn_select_image.setOnClickListener(clickListener);
        btn_discriminate.setOnClickListener(clickListener);
//        init();
        template = BitmapFactory.decodeResource(getResources(), R.drawable.te);

        initTess();
    }
    private void initTess() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                showProgress();
            }

            @Override
            protected void onPostExecute(Boolean result) {
                dismissProgress();
                if (result == null || !result) {

                    Toast.makeText(MainActivity.this, "load trainedData failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                baseApi = new TessBaseAPI();
                try {
                    InputStream is = null;
                    is = getAssets().open(language + ".traineddata");

                    File dir = new File("/sdcard/tess/tessdata");
                    if (!dir.exists()){
                        dir.mkdirs();
                    }
                    File file = new File(dir, language + ".traineddata");
                    if (!file.exists()) {

                        FileOutputStream fos = new FileOutputStream(file);
                        byte[] buffer = new byte[2048];
                        int len;
                        while ((len = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }
                        fos.close();
                    }
                    is.close();
                    return baseApi.init("/sdcard/tess", language);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        }.execute();
    }
    void init(){
        showProgress();
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                baseApi = new TessBaseAPI();
                try {
                    InputStream is = null;
                    is = getAssets().open(language + ".traineddata");
                    File file = new File("/sdcard/tess/tessdata/" + language + ".traineddata");
                    if (!file.exists()) {
                        file.getParentFile().mkdirs();
                        FileOutputStream fos = new FileOutputStream(file);
                        byte[] buffer = new byte[2048];
                        int len;
                        while ((len = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }
                        fos.close();
                    }
                    is.close();
                   boolean result =   baseApi.init("/sdcard/tess", language);
                    subscriber.onNext(result ?1:0);
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onNext(0);

                }
            }
        }).subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                dismissProgress();
            }

            @Override
            public void onError(Throwable e) {
                dismissProgress();
            }

            @Override
            public void onNext(Integer integer) {
                dismissProgress();
                if (integer ==1){
                    showCustomToast("加载成功");
                }else {
                    showCustomToast("加载失败");

                }
            }
        });
    }

    private ProgressDialog progressDialog;

    private void showProgress() {
        if (null != progressDialog) {
            progressDialog.show();
        } else {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("请稍候...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    private void dismissProgress() {
        if (null != progressDialog) {
            progressDialog.dismiss();
        }
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_select_image:
                    requestImages();
                    break;

                case R.id.btn_discriminate:
                    recognition();
                    break;
            }
        }
    };
    List<String> upoloadImgs;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_HEAD_IMAGE){
            if(resultCode == RESULT_OK){
                if (upoloadImgs != null){
                    upoloadImgs.clear();
                    upoloadImgs = null;
                }
                upoloadImgs = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                if (upoloadImgs !=null &&upoloadImgs.size()>0){
                    String imagePath  = upoloadImgs.get(0);
                    if (!TextUtils.isEmpty(imagePath)) {
                        if (fullImage != null) {
                            fullImage.recycle();
                        }
                        int degree = BmpUtil.readPictureDegree(imagePath);
                        fullImage = toBitmap(imagePath);
                        Log.e("hss","degree =  "+degree);
                        if (degree !=0){
                            fullImage= BmpUtil.rotateBitmap(fullImage,-degree);
                        }
                        tv_result.setText(null);
                        imageView.setImageBitmap(fullImage);
                    }
                }

            }
        } else  {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }
    void requestImages(){
        getPermission().request(Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        )
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {//AssessQuestionFragment
                        if (aBoolean){
                            Intent intent = new Intent(MainActivity.this, MultiImageSelectorActivity.class);
                            intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
                            intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
                            startActivityForResult(intent, MainActivity.REQUEST_HEAD_IMAGE);
                        }else {
                            showCustomToast("请打开读写sd卡和调用相机的权限");
                        }

                    }
                });

    }
    RxPermissions rxPermissions ; // where this is an Activity instance
    public RxPermissions getPermission() {
        if (rxPermissions == null){
            rxPermissions = new RxPermissions(this); // where this is an Activity instance
        }
        return rxPermissions;
    }
    void showCustomToast(String str){
        Toast.makeText(getApplicationContext(),str,Toast.LENGTH_SHORT).show();
    }

    void recognition(){
        tv_result.setText(null);
        Bitmap bitmapResult = ImageProcess.getIdNumber(fullImage, template, Bitmap.Config.ARGB_8888);
        if (fullImage !=null){
            fullImage.recycle();
        }

        //tesseract-ocr
        imageView.setImageBitmap(bitmapResult);
        // 识别Bitmap中的图片
        baseApi.setImage(bitmapResult);
        tv_result.setText(baseApi.getUTF8Text());
        baseApi.clear();
    }

    public static Bitmap toBitmap(String pathName) {
        if (TextUtils.isEmpty(pathName))
            return null;
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, o);
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp <= 640 && height_tmp <= 480) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = scale;
        opts.outHeight = height_tmp;
        opts.outWidth = width_tmp;
        return BitmapFactory.decodeFile(pathName, opts);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgress();
        baseApi.end();
        template.recycle();
    }
}
