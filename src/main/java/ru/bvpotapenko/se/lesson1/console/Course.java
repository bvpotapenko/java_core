package ru.bvpotapenko.se.lesson1.console;

public class Course {
    int[] obstacles;
    int maxFailsAllowed;

    public Course() {
        this.obstacles = new int []{1, 3, 2, 5, 4};
        int maxFailsAllowed = 3;
    }

    public Course(int[] obstacles, int maxFailsAllowed) {
        this.obstacles = obstacles;
        this.maxFailsAllowed = maxFailsAllowed;
    }

    public void doIt(Team team){
        team.qualified.clear();
        for(TeamMember t : team.teamMembers){
            if (t == null) continue;
            int fails = 0; //Times sportsman failed on the course
            int stamina = t.getStamina();
            for(int obstacle : obstacles){
                //Not strong enough to overcome an obstacle or too tired
                if (obstacle > t.getPower() || stamina <= 0) fails++;
                stamina = stamina - obstacle;
            }
            if (fails <= maxFailsAllowed)
                team.qualified.add(t);
        }
    }

    public int getMaxFailsAllowed() {
        return maxFailsAllowed;
    }

    public int[] getObstacles() {
        return obstacles;
    }
}
