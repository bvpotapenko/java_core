package ru.bvpotapenko.se.lesson1.console;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        TeamMember t1 = new TeamMember("Semen", "Smirnov", 14, 6);
        TeamMember t2 = new TeamMember("John", "Black", 19, 4);
        TeamMember t3 = new TeamMember("Santa", "Claus", 20, 7);

        Course c = new Course(); // Создаем полосу препятствий
        Team team_1 = new Team("Mighty nachos", t1, t2, t3);  // Создаем команду 2
        Team team_2 = new Team("Over powerful anonymous");  // Создаем команду 2
        c.doIt(team_1);               // Просим команду пройти полосу
        c.doIt(team_2);               // Просим команду пройти полосу
        team_1.showResults();         // Показываем результаты
        System.out.println("*******");
        team_2.showResults();         // Показываем результаты

    }

}
