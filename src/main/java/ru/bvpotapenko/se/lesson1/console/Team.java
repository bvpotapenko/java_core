package ru.bvpotapenko.se.lesson1.console;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private String teamName;
    List<TeamMember> teamMembers;
    List<TeamMember> qualified;

    Team (String name, TeamMember...team){
        this.teamName = name;
        teamMembers = new ArrayList<>(4);
        qualified = new ArrayList<>(4);
        for(int i = 0; i < 4; i++){
            if( i <= team.length - 1 && team[i] != null )
                teamMembers.add(team[i]);
            else //Add an anonymous
                teamMembers.add(new TeamMember());
        }
    }

    public void showResults(){
        System.out.println("Team \'"+ teamName+"\' [qualified members]:");
        qualified.forEach(System.out::println);
    }

    public void showTeam(){
        System.out.println("Team \'"+ teamName+"\' [all members]:");
        teamMembers.forEach(System.out::println);
    }

    public String getTeamName() {
        return teamName;
    }
}
