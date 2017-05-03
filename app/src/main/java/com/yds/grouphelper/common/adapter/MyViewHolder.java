package com.yds.grouphelper.common.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public final class MyViewHolder extends RecyclerView.ViewHolder {

    public MyViewHolder(View view) {
        super(view);
    }

    public View getView(int id){
        return this.itemView.findViewById(id);
    }

    public void setText(int id,int resID){
        String text=this.itemView.getContext().getString(resID);
        if(!TextUtils.isEmpty(text)){
            setText(id,text);
        }
    }

    public void setText(int id,String text){
        View view=getView(id);
        if(view instanceof TextView){
            ((TextView)view).setText(text);
        }else if(view instanceof Button){
            ((Button)view).setText(text);
        }else if(view instanceof EditText){
            ((EditText)view).setText(text);
        }
    }

    public void setText(int id,Spanned spanned){
        View view=getView(id);
        if(view instanceof TextView){
            ((TextView)view).setText(spanned);
        }else if(view instanceof Button){
            ((Button)view).setText(spanned);
        }else if(view instanceof EditText){
            ((EditText)view).setText(spanned);
        }
    }

    public void setImageResource(int id,int resID){
        View view=getView(id);
        if(view instanceof ImageView){
            ((ImageView)view).setImageResource(resID);
        }
    }

    public void setTag(int id,Object object){
        getView(id).setTag(object);
    }

}
