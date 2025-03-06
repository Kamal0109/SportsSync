package com.mtm.captaincalling;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mtm.captaincalling.Interface.ItemClickListener;

public class ViewTournamentTeamsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView viewTournamentTeamName, viewTournamentTeamCaptainName;
    public LinearLayout teamsLayout;
    public ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ViewTournamentTeamsViewHolder(@NonNull View itemView) {
        super(itemView);

        viewTournamentTeamName = itemView.findViewById(R.id.view_team_name);
        viewTournamentTeamCaptainName = itemView.findViewById(R.id.view_team_captain_name);

        teamsLayout = itemView.findViewById(R.id.team_detail_layout);

    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }
}
