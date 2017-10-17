package com.lockscreenplayer.js.lockscreenplayer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
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
public class GoodsViewHolder extends RecyclerView.ViewHolder {

    public TextView GoodsName, GoodsPrice, GoodsBrand;
    public ImageView GoodsImage;

    public GoodsViewHolder(View itemView) {
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


class GoodsAdapter extends RecyclerView.Adapter<GoodsViewHolder> implements GoodsViewHolder.OnListItemClickListener{
    Context context;
    public GoodsAdapter(Context context) {
        this.context = context;
    }


    @Override
    public GoodsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.goods_item,parent,false);
        GoodsViewHolder holder = new GoodsViewHolder(v);
        holder.setOnListItemClickListener(this);
        return holder;

    }
    //어댑터아이템 추가후 데이터 반영
    List<goods_item> items = new ArrayList<>();
    public void add(goods_item data){
        items.add(data);

    }


    @Override
    public void onBindViewHolder(GoodsViewHolder holder, int position) {
        //뷰홀더를 데이터와 바인딩 , 데이터를 뷰에 그려준다
        goods_item item = items.get(position);
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


    //리스트아이템 클릭이벤트
    @Override
    public void onListItemClick(int position) {
//        Toast.makeText(context,"상품명 : "+items.get(position).getName()+"     " +
//                "position : "+position,Toast.LENGTH_SHORT).show();

       //상세페이지(구매)로 상품정보전달
        Intent intent = new Intent(context,GoodsDetailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("name",items.get(position).getName());
        intent.putExtra("price",items.get(position).getPrice());
        //intent.putExtra("category",items.get(position).getName());
        intent.putExtra("image",items.get(position).getImage());
        intent.putExtra("brand",items.get(position).getBrand());

        context.startActivity(intent);

    }
}

class goods_item {
    private String name, brand;
    private int price;
    private String image ;

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


}