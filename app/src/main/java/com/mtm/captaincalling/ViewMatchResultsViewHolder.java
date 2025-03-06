package com.mtm.captaincalling;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewMatchResultsViewHolder extends RecyclerView.ViewHolder {

    public TextView NoMatchesYet, MatchNoText, Team1NameText, Team2NameText, MatchResultText;

    public ViewMatchResultsViewHolder(@NonNull View itemView) {
        super(itemView);

        NoMatchesYet = itemView.findViewById(R.id.no_matches_yet);
        MatchNoText = itemView.findViewById(R.id.match_no_text);
        Team1NameText = itemView.findViewById(R.id.team_name_1_text);
        Team2NameText = itemView.findViewById(R.id.team_name_2_text);
        MatchResultText = itemView.findViewById(R.id.match_results_text);

    }
}
