package ru.bvpotapenko.se.lesson1.console;

import java.util.Random;

public class TeamMember {
    private static int index = 0; //to enumerate team members

    private final String givenName;
    private final String familyName;
    private int stamina; //Overall performance ability on a course
    private int power; //Maximum obstacle without failing
    private final int number; //Unique personal number
    private int failsMade;

    @Override
    public String toString() {
        return givenName + " " + familyName + "(#"+number+"):\n" +
                "  Stamina: " + stamina + "\n" +
                "  Power: " + power;
    }

    public TeamMember() {
        Random random = new Random();
        this.givenName = "Anonymous";
        this.familyName = getRandomString();
        this.stamina = random.nextInt(20)+10;
        this.power = random.nextInt(7) + 3;
        failsMade = 0;
        index++;
        number = index;
    }

    public TeamMember(String givenName, String familyName, int stamina, int power) {
        this.givenName = givenName;
        this.familyName = familyName;
        this.stamina = stamina;
        this.power = power;
        failsMade = 0;
        index++;
        number = index;
    }

    public int getNumber() {
        return number;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getFamilyName() {
        return familyName;
    }
    public String getFullName(){
        return givenName + " " + familyName;
    }

    public int getStamina() {
        return stamina;
    }

    public int getPower() {
        return power;
    }

    public int getFailsMade() {
        return failsMade;
    }

    public void setFailsMade(int failsMade) {
        this.failsMade = failsMade;
    }

    //Make a random name
    private String getRandomString(){
        StringBuilder buffer = new StringBuilder(6);
        buffer.append(getRandomStringInABound(65,90, 1)); //Capital Letter
        buffer.append(getRandomStringInABound(97,122, 5));
        return buffer.toString();
    }

    private String getRandomStringInABound(int leftBound, int rightBound, int stringLength){
        int leftLimit = leftBound;
        int rightLimit = rightBound;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(stringLength);
        for (int i = 0; i < stringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

    public void setStamina(int stamina) {
        this.stamina = stamina;
    }
}
