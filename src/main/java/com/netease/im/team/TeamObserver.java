package com.netease.im.team;

import com.netease.im.ReactCache;
import com.netease.im.uikit.cache.TeamDataCache;
import com.netease.im.uikit.contact.core.model.ContactDataList;
import com.netease.nimlib.sdk.team.model.Team;

import java.util.List;

/**
 * Created by dowin on 2017/5/9.
 */

public class TeamObserver {


    TeamListService teamListService = TeamListService.getInstance();
    TeamDataCache.TeamDataChangedObserver teamDataChangedObserver=new TeamDataCache.TeamDataChangedObserver() {
        @Override
        public void onUpdateTeams(List<Team> teams) {
            teamListService.load(true);
        }

        @Override
        public void onRemoveTeam(Team team) {
            teamListService.load(true);
        }
    };
    public void startTeamList(){
        queryTeamList();
        registerObserver(true);
    }
    public void stopTeamList(){
        registerObserver(false);
    }
    void queryTeamList(){
        teamListService.setOnLoadListener(new TeamListService.OnLoadListener() {
            @Override
            public void updateData(ContactDataList datas) {
                ReactCache.emit(ReactCache.observeTeam,ReactCache.createTeamList(datas));
            }
        });
        teamListService.load(true);
    }
    boolean hasRegister;
    void registerObserver(boolean register){
        if(hasRegister&&register){
            return;
        }
        hasRegister = register;
        if(register) {
            TeamDataCache.getInstance().registerTeamDataChangedObserver(teamDataChangedObserver);
        }else {
            TeamDataCache.getInstance().unregisterTeamDataChangedObserver(teamDataChangedObserver);
        }

    }
}
