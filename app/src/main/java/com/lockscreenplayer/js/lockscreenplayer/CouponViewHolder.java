package com.lockscreenplayer.js.lockscreenplayer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2017-02-27.
 */
public class CouponViewHolder extends RecyclerView.ViewHolder {

    public TextView GoodsName, GoodsPrice, GoodsBrand;
    public ImageView GoodsImage;

    public CouponViewHolder(View itemView) {
        super(itemView);
        GoodsName = (TextView)itemView.findViewById(R.id.goodsname);
        GoodsPrice = (TextView)itemView.findViewById(R.id.goodsprice);
        GoodsBrand = (TextView)itemView.findViewById(R.id.goodsbrand);
        GoodsImage = (ImageView)itemView.findViewById(R.id.goods_img);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onListItemClick(getAdapterPosition());
            }
        });
    }

    public interface OnListItemClickListener {
        public void onListItemClick(int position);

    }
    OnListItemClickListener mListener;
    public void setOnListItemClickListener(OnListItemClickListener onListItemClickListener) {
        mListener = onListItemClickListener;
    }
}


class CouponAdapter extends RecyclerView.Adapter<CouponViewHolder> implements CouponViewHolder.OnListItemClickListener{
    Context context;
    public CouponAdapter(Context context) {
        this.context = context;
    }


    @Override
    public CouponViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.goods_item,parent,false);
        CouponViewHolder holder = new CouponViewHolder(v);
        holder.setOnListItemClickListener(this);
        return holder;

    }
    //?????????????????? ????????? ????????? ??????
    List<coupon_item> items = new ArrayList<>();
    public void add(coupon_item data){
        items.add(data);

    }


    @Override
    public void onBindViewHolder(CouponViewHolder holder, int position) {
        //???????????? ???????????? ????????? , ???????????? ?????? ????????????
        coupon_item item = items.get(position);
        holder.GoodsName.setText(item.getName());
        holder.GoodsPrice.setText(Comma_won(item.getPrice()+"")+" P");
        holder.GoodsBrand.setText(item.getBrand());

        Glide.with(context).load(Server.localhost+"/img/"+item.getImage()+".jpg").into(holder.GoodsImage);

        //holder.GoodsImage.setImageURI(item.getName());


    }
    public static String Comma_won(String junsu) {
        int inValues = Integer.parseInt(junsu);
        DecimalFormat Commas = new DecimalFormat("#,###");
        String result_int = (String)Commas.format(inValues);
        return result_int;
    }
    @Override
    public int getItemCount() {
        return items.size();
    }

    public  void clear() {
        items.removeAll(items);

    }
home home;

    //?????????????????? ???????????????
    @Override
    public void onListItemClick(int position) {

//        Toast.makeText(context,"????????? : "+items.get(position).getName()+"     " +
//                "position : "+position,Toast.LENGTH_SHORT).show();
        Log.d("???????????????","?????? "+items.get(position).getName());
        //???????????????(??????)??? ??????????????????
        Intent intent = new Intent(context,CouponDetailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("name",items.get(position).getName());
        intent.putExtra("price",items.get(position).getPrice());
        //intent.putExtra("category",items.get(position).getName());
        intent.putExtra("image",items.get(position).getImage());
        intent.putExtra("brand",items.get(position).getBrand());
        intent.putExtra("couponnum",items.get(position).getCouponnum());
        intent.putExtra("validity",items.get(position).getValidity());
        Log.d("???????????????","??????");
        context.startActivity(intent);
    }
}

class coupon_item {
    private String name, brand,image, couponnum,validity;
    private int price;

    public String getBrand() {
        return brand;
    }
    public String getName() {
        return name;
    }
    public int getPrice() {
        return price;
    }
    public String getImage() {
        return image;
    }
    public String getCouponnum() {
        return couponnum;
    }
    public String getValidity() { return validity; }

    public void setBrand(String brand) {
        this.brand = brand;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public void setCouponnum(String couponnum) {
        this.couponnum = couponnum;
    }
    public void setValidity(String validity) { this.validity = validity; }
}