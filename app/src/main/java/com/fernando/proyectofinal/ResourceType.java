package com.fernando.proyectofinal;

import androidx.annotation.NonNull;

public enum ResourceType {
    TAG(0),
    PERSON(1),
    GARDEN(2),
    WEATHER(3),
    LOCATION(4),
    PLANT(5),
    ARTICLE(6),
    SHARED_VIEW(7);

    private final int value;
    private ResourceType(int value) {
        this.value = value;
    }

    public int getValue() { return value; }

    @NonNull
    @Override
    public String toString() {
        switch(this.value) {
            case 0: return "Tag";
            case 1: return "Persona";
            case 2: return "Jardín";
            case 3: return "Clima";
            case 4: return "Ubicación";
            case 5: return "Planta";
            case 6: return "Artículo";
            case 7: return "Shared View";
            default: return "Entidad Default";
        }
    }

    public static String toString(int value) {
        switch(value) {
            case 0: return "Tag";
            case 1: return "Persona";
            case 2: return "Jardín";
            case 3: return "Clima";
            case 4: return "Ubicación";
            case 5: return "Planta";
            case 6: return "Artículo";
            case 7: return "Shared View";
            default: return "Entidad Default";
        }
    }
}
