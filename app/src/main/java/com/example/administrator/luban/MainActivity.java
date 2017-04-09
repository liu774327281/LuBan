package com.example.administrator.luban;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class MainActivity extends AppCompatActivity{
    private Uri faceimageUri;
    private Uri newfaceimageUri;
    private ImageView photo;
    private File outputImage;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        photo = (ImageView) findViewById(R.id.photo);
    }

    public void photo(View view){
        outputImage = new File(Environment.getExternalStorageDirectory(), "output_image.jpg");
        try{
            if(outputImage.exists()){
                outputImage.delete();
            }
            outputImage.createNewFile();
        }catch(IOException e){
            e.printStackTrace();
        }
        if(Build.VERSION.SDK_INT >= 24){
            faceimageUri = FileProvider.getUriForFile(MainActivity.this, "com", outputImage);
        }else{
            faceimageUri = Uri.fromFile(outputImage);
        }
        Log.d("MainActivity", "outputImage:" + outputImage);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, faceimageUri);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        switch(requestCode){

            case 1:
                if(resultCode == RESULT_OK){
                    String path = faceimageUri.getPath();
                    Log.d("MainActivity", path);
                    File file = new File(path);
                    Luban.get(this).load(file)                     //传人要压缩的图片
                            .putGear(Luban.THIRD_GEAR)      //设定压缩档次，默认三挡
                            .setCompressListener(new OnCompressListener(){ //设置回调

                                @Override
                                public void onStart(){
                                    // TODO 压缩开始前调用，可以在方法内启动 loading UI
                                }

                                @Override
                                public void onSuccess(File file){
                                    // TODO 压缩成功后调用，返回压缩后的图片文件
                                    Log.d("MainActivity", "压缩成功");
                                    Log.d("MainActivity", "newfile:" + file);
                                    if(Build.VERSION.SDK_INT >= 24){
                                        newfaceimageUri = FileProvider.getUriForFile(MainActivity.this, "com", file);
                                    }else{
                                        newfaceimageUri = Uri.fromFile(file);
                                    }
                                    Bitmap facebitmap = null;
                                    try{
                                        facebitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(newfaceimageUri));
                                    }catch(FileNotFoundException e){
                                        e.printStackTrace();
                                    }
                                    //  facepath = faceimageUri.getPath();
                                    //  final String compress = compress(path, 50, 50);
                                    Log.d("MainActivity", "facebitmap.getByteCount():" + facebitmap.getByteCount() / 1024 / 1024);
                                    Log.d("MainActivity", "facebitmap.getByteCount():" + facebitmap.getByteCount());
                                    photo.setImageBitmap(facebitmap);
                                }

                                @Override
                                public void onError(Throwable e){
                                    // TODO 当压缩过去出现问题时调用
                                }
                            }).launch();
                }
                break;
        }
    }
}
