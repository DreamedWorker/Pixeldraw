package com.dream.pixeldraw;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Process;
import android.util.Log;

import java.io.*;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

import androidx.annotation.RequiresApi;

public abstract class AppGlobalData {
    public static Context MAIN_CONTEXT;
    public static MainActivity MA_INSTANCE;
    public static ArrayList<Bitmap> Plates=new ArrayList<>();
    public static ArrayList<Bitmap> Saved_Plates=new ArrayList<>();
    public static Bitmap colorful_bar;
    public static Bitmap color_plane=Bitmap.createBitmap(800,800,Bitmap.Config.ARGB_8888);
    public static Bitmap alpha_plane=Bitmap.createBitmap(870,300,Bitmap.Config.ARGB_8888);
    public static Bitmap brightness_plane=Bitmap.createBitmap(870,300,Bitmap.Config.ARGB_8888);
    public static Bitmap copied_pic;
    private static File bitmap_file,collection_file;
    private static Drawable black;
    private static Drawable white;
    public static void initailizeData(){
        bitmap_file=new File(MAIN_CONTEXT.getFilesDir().getAbsolutePath()+"/recent.png");
        collection_file=new File(MAIN_CONTEXT.getFilesDir().getAbsolutePath()+"/color.txt");
        MA_INSTANCE.getWindowManager().getDefaultDisplay().getMetrics(MA_INSTANCE.displayMetrics);
        try {
            if(!bitmap_file.exists()){
                bitmap_file.createNewFile();
                FileOutputStream os=new FileOutputStream(bitmap_file);
                Bitmap.createBitmap(16,16, Bitmap.Config.ARGB_8888).compress(Bitmap.CompressFormat.PNG,80,os);
                os.flush();
                os.close();
            }
            if(!collection_file.exists()){
                collection_file.createNewFile();
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public static void addColColors(int color){
        try {
            FileWriter fw=new FileWriter(collection_file);
            if(collection_file.length()==0)
                fw.write(""+color);
            else
                fw.write(","+color);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static int[] getColColors(){
        ArrayList<Integer> colors=new ArrayList<>();

        try {
            FileReader fr=new FileReader(collection_file);
            int buff;
            String color="";
            while((buff=fr.read())!=-1){
                if((char)buff == ','){
                    colors.add(Integer.parseInt(color));
                    Log.d("colcolor", color);
                    color="";
                }else
                    color+=(char)buff;
            }
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int[] res=new int[colors.size()];
        for(int i=0;i<res.length;i++) res[i]=colors.get(i);
        return res;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Bitmap initColorfulBar(){
        //color
        colorful_bar= Bitmap.createBitmap(300,870,Bitmap.Config.ARGB_8888);
        for(int i=0;i<870;i++)
            for(int j=0;j<300;j++){
                    float sum = (float) i / 870f;
                    colorful_bar.setPixel(j, i, Color.HSVToColor(255,new float[]{sum*360,1,1}));
            };

        black=new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,new int[]{Color.TRANSPARENT,Color.BLACK});
        white=new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,new int[]{Color.TRANSPARENT,Color.WHITE});

        return colorful_bar;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static Bitmap makeColorPlane(int color){
        Drawable d=new ColorDrawable(color);
        LayerDrawable l= new LayerDrawable(new Drawable[]{d, black, white});

        Canvas canvas=new Canvas(color_plane);
        l.setBounds(0,0,800,800);
        l.draw(canvas);
        return color_plane;
    }
    public static Bitmap makeAlphaPlane(int color){
        int turned_color=Color.argb(0,Color.red(color),Color.green(color),Color.blue(color));
        GradientDrawable ll=new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,new int[]{color,turned_color});
        LayerDrawable l=new LayerDrawable(new Drawable[]{MA_INSTANCE.getResources().getDrawable(R.drawable.trans,null),ll});
        Canvas canvas=new Canvas(alpha_plane);
        l.setBounds(0,0,900,300);
        l.draw(canvas);
        return alpha_plane;
    }
    public static Bitmap makeBrightnessPlane(int color){
        float[] hsv=new float[3];
        Color.colorToHSV(color,hsv);
        hsv[2]=1f;
        GradientDrawable l=new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,new int[]{Color.BLACK,Color.HSVToColor(Color.alpha(color),hsv),Color.WHITE});
        Canvas canvas=new Canvas(brightness_plane);
        l.setBounds(0,0,900,300);
        l.draw(canvas);
        return brightness_plane;
    }
    public static void cancelApplication(){
        Process.killProcess(Process.myPid());
    }
    public static void restartApplication(Activity instance){
        final Intent intent=instance.getPackageManager().getLaunchIntentForPackage(MA_INSTANCE.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        instance.startActivity(intent);
    }
}
