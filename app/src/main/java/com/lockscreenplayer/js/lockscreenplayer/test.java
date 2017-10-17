//package com.lockscreenplayer.js.lockscreenplayer;
//
//import android.content.Context;
//import android.os.Parcelable;
//import android.support.v4.view.PagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.view.LayoutInflater;
//import android.view.View;
//
//public class test extends PagerAdapter {// ViewPager 쓰듯이 쓰면 되는것이다.
//
//    private LayoutInflater mInflater;
//
//
//    private int orientation;
//
//
//    private int []xpos = {R.layout.fragment_lock_pager,R.layout.fragment_lock_pager,R.layout.fragment_lock_pager};
//
//
//    private int []ypos = {R.layout.home,R.layout.fragment_lock_pager,R.layout.nav_header_lock_screen};
//
//
//    public test(Context con, int orientation) {
//
//
//        super();
//
//
//        mInflater = LayoutInflater.from(con);
//
//
//        this.orientation = orientation;
//
//
//    }
//
//
//    @Override public int getCount() {
//        return xpos.length; } //여기서는 2개만 할 것이다.
//
//
////뷰페이저에서 사용할 뷰객체 생성/등록
//
//
//    @Override public Object instantiateItem(View pager, int position) {
//
//
//        View view = null;
//
//
//        if(orientation==0){ // 가로
//
//
//            view = mInflater.inflate(xpos[position], null);
//
//
//        }
//
//
//        else{// 세로
//
//
//            view = mInflater.inflate(ypos[position], null);
//
//
//        }
//
//
//        ((ViewPager)pager).addView(view, 0);
//
//
//        return view;
//
//
//    }
//
//
////뷰 객체 삭제.
//
//
//    @Override public void destroyItem(View pager, int position, Object view) {
//
//
//        ((ViewPager)pager).removeView((View)view);
//
//
//    }
//
//
//// instantiateItem메소드에서 생성한 객체를 이용할 것인지
//
//    @Override public boolean isViewFromObject(View view, Object obj) { return view == obj; }
//
//
//    @Override public void finishUpdate(View arg0) {}
//
//
//    @Override public void restoreState(Parcelable arg0, ClassLoader arg1) {}
//
//
//    @Override public Parcelable saveState() { return null; }
//
//
//    @Override public void startUpdate(View arg0) {}
//
//
//}