package com.mtm.captaincalling;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mtm.captaincalling.Interface.ItemClickListener;

public class SendTournamentRequestsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView sendTournamentTeamName, sendTournamentSport, sendTournamentTeamCaptianName, sendTournamentLevel, sendTournamentState, sendTournamentDistrict, sendTournamentAddress, sendTournamentButton;
    ItemClickListener itemClickListener;

    public ImageView teamPic;

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }
    public SendTournamentRequestsViewHolder(@NonNull View itemView) {
        super(itemView);

        sendTournamentTeamName = itemView.findViewById(R.id.send_tournament_team_name);
        sendTournamentTeamCaptianName = itemView.findViewById(R.id.send_tournament_request_team_captain);
        sendTournamentSport = itemView.findViewById(R.id.send_tournament_sport);
        sendTournamentDistrict = itemView.findViewById(R.id.send_tournament_district);
        sendTournamentAddress = itemView.findViewById(R.id.send_tournament_address);
        sendTournamentLevel = itemView.findViewById(R.id.send_tournament_level);
        sendTournamentState = itemView.findViewById(R.id.send_tournament_state);
        sendTournamentButton = itemView.findViewById(R.id.send_tournament_button);
        teamPic = itemView.findViewById(R.id.team_pic_add_at);
    }

    @Override
    public void onClick(View view) {
        if(itemClickListener != null){
            itemClickListener.onClick(view, getAdapterPosition(), false);
        }
    }
}
