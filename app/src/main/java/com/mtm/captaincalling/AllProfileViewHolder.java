package com.mtm.captaincalling;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mtm.captaincalling.R;
import com.mtm.captaincalling.Interface.ItemClickListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllProfileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView priceText, address, address_text, info_text, additional_info_text, district,level,name,phone,psport,ssport,state,addBtn,cancelBtn,level2,expertise, roleText, accept, reject;
    public CircleImageView circleImageView;
    public ItemClickListener itemClickListener;
    public LinearLayout full_layout, linearLayout, viewAchievements, viewSkillVideoLink1, viewSkillVideoLink2, viewSkillVideoLink3, additional_info_layout;
    public RelativeLayout relativeLayout;
    public ImageView delete,call,captain;
    public LinearLayout level_lyt;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public AllProfileViewHolder(@NonNull View itemView) {
        super(itemView);

        address = itemView.findViewById(R.id.address_add);

        address_text = itemView.findViewById(R.id.address_text);
        info_text = itemView.findViewById(R.id.info_text);
        additional_info_text = itemView.findViewById(R.id.additional_info);

        additional_info_layout = itemView.findViewById(R.id.additional_info_layout);

        full_layout = itemView.findViewById(R.id.full_layout);

        district = itemView.findViewById(R.id.district_add);
        level = itemView.findViewById(R.id.leve_add);
        name = itemView.findViewById(R.id.player_name_add);
        phone = itemView.findViewById(R.id.phone_add);
        psport = itemView.findViewById(R.id.primary_add);
        ssport = itemView.findViewById(R.id.secondary_add);
        state = itemView.findViewById(R.id.state_add);
        circleImageView = itemView.findViewById(R.id.team_pic_add);
        linearLayout = itemView.findViewById(R.id.ly_add);
        addBtn = itemView.findViewById(R.id.add_btn);
        relativeLayout = itemView.findViewById(R.id.mkbjvhchchchc);
        cancelBtn = itemView.findViewById(R.id.cancel_btn);
        delete = itemView.findViewById(R.id.delete_player);
        call = itemView.findViewById(R.id.call_player);
        captain = itemView.findViewById(R.id.captain_logo);
        level2 = itemView.findViewById(R.id.level_second);
        level_lyt = itemView.findViewById(R.id.level_lyt);
        expertise = itemView.findViewById(R.id.expert_input);

        viewAchievements = itemView.findViewById(R.id.viewAchievements);

        viewSkillVideoLink1 = itemView.findViewById(R.id.skillVideoLink1);
        viewSkillVideoLink2 = itemView.findViewById(R.id.skillVideoLink2);
        viewSkillVideoLink3 = itemView.findViewById(R.id.skillVideoLink3);

        roleText = itemView.findViewById(R.id.role_text);

        accept = itemView.findViewById(R.id.accept_btn);
        reject = itemView.findViewById(R.id.reject_btn);

        priceText = itemView.findViewById(R.id.price);

        itemView.setOnClickListener(this);

//        itemView.setVisibility(View.GONE);

    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);

    }
}
