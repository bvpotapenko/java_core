package ru.bvpotapenko.se.collections.phonebook;

import java.util.*;
import java.util.stream.Collectors;

public class Phonebook {
    private Map<String, List<String>> phonebook;

    public Phonebook() {
        this.phonebook = new HashMap<>();
    }

    public Phonebook(Map<String, List<String>> phonebook) {
        this.phonebook = phonebook;
    }

    public void add(String lastname, String phoneNumber) {
        if (lastname == null || phoneNumber == null) return;
        ArrayList<String> sArr = new ArrayList<>();
        sArr.add(phoneNumber);
        if (phonebook.get(lastname) != null)
            sArr.addAll(phonebook.get(lastname));

        phonebook.put(lastname, sArr);
    }

    public List<String> getByLastname(String lastname) {
        return phonebook.get(lastname);
    }

    @Override
    public String toString() {
        StringBuilder asString = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : phonebook.entrySet()){
            asString.append(entry.getKey());
            asString.append(" : ");
            asString.append(entry.getValue()
                                    .stream()
                                    .map(String::valueOf)
                                    .collect(Collectors.joining(",\n    ", "\n  {\n    ", "\n  }\n")));
        }
        return asString.toString();
    }
}
