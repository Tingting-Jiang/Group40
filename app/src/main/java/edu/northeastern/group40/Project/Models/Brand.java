package edu.northeastern.group40.Project.Models;

public enum Brand {


    HONDA(Model.ACCORD, Model.CIVIC),
    TOYOTA(Model.CAMRY, Model.COROLLA);

    private final Model[] models;

    private Brand(Model... models) {
        this.models = models;
    }

    public Model[] getModels(){
        return models;
    }

    public enum Model {
        //HONDA
        CIVIC,
        ACCORD,

        //TOYOTA
        CAMRY,
        COROLLA,

    }
}
