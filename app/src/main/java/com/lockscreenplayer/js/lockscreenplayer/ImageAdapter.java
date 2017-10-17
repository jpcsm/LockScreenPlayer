package com.lockscreenplayer.js.lockscreenplayer;
//
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.support.v4.view.PagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//
//public class ImageAdapter extends PagerAdapter {
//
//    Context context;
//    Bitmap galImage;
//    BitmapFactory.Options options;
//    private final int[] galImages = new int[] {
//            R.drawable.ic_menu_camera,
//            R.drawable.ic_menu_gallery,
//            R.drawable.ic_menu_manage,
//
//    };
//
//    ImageAdapter(Context context) {
//        this.context = context;
//        options = new BitmapFactory.Options();
//    }
//
//    @Override
//    public int getCount() {
//        return galImages.length;
//    }
//
//    @Override
//    public boolean isViewFromObject(View view, Object object) {
//        return view == ((ImageView) object);
//    }
//
//    @Override
//    public Object instantiateItem(ViewGroup container, int position) {
//        ImageView imageView = new ImageView(context);
//        int padding = context.getResources().getDimensionPixelSize(R.dimen.appbar_padding_top);
//        imageView.setPadding(padding, padding, padding, padding);
//        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//
//        options.inSampleSize = 4;
//        galImage = BitmapFactory.decodeResource(context.getResources(), galImages[position], options);
//
//        imageView.setImageBitmap(galImage);
//        ((ViewPager) container).addView(imageView, 0);
//        return imageView;
//    }
//
//    @Override
//    public void destroyItem(ViewGroup container, int position, Object object) {
//        ((ViewPager) container).removeView((ImageView) object);
//    }
//}