package edu.northeastern.group40.Project.Models;

public enum Brand {


    HONDA(Model.ACCORD, Model.CIVIC, Model.CR_V, Model.FIT, Model.ODYSSEY, Model.PILOT, Model.RIDGELINE, Model.CITY, Model.BRIO, Model.AMZE),
    TOYOTA(Model.CAMRY, Model.COROLLA, Model.RAV4, Model.PRIUS, Model.YARIS, Model.HIGHLANDER, Model.TACOMA, Model.SIENNA, Model.AVALON, Model.SUPRA),
    FORD(Model.FOCUS, Model.FIESTA, Model.FUSION, Model.MUSTANG, Model.ESCAPE, Model.EDGE, Model.EXPLORER, Model.RANGER, Model.EXPEDITION, Model.TAURUS),
    CHEVROLET(Model.CRUZE, Model.MALIBU, Model.IMPALA, Model.CAMARO, Model.EQUINOX, Model.TRAVERSE, Model.COLORADO, Model.SILVERADO, Model.SUBURBAN, Model.TAHOE),
    NISSAN(Model.ALTIMA, Model.MAXIMA, Model.ROGUE, Model.SENTRA, Model.VERSA, Model.MURANO, Model.PATHFINDER, Model.TITAN, Model.ARMADA, Model.KICKS),
    BMW(Model.X1, Model.X3, Model.X5, Model.X6, Model.Z4, Model.I3, Model.I8),
    MERCEDES_BENZ(Model.C_CLASS, Model.E_CLASS, Model.S_CLASS, Model.A_CLASS, Model.CLASSE_GLA, Model.GLE_CLASS, Model.SLC_CLASS, Model.GLK_CLASS, Model.GL_CLASS, Model.SLS_CLASS),
    AUDI(Model.A3, Model.A4, Model.A5, Model.A6, Model.A7, Model.A8, Model.Q3, Model.Q5, Model.Q7, Model.R8),
    LEXUS(Model.ES, Model.IS, Model.LC, Model.LS, Model.LX, Model.NX, Model.RC, Model.RX, Model.UX, Model.GX),
    MAZDA(Model.MAZDA3, Model.MAZDA6, Model.MX5_MIATA, Model.CX3, Model.CX5, Model.CX9, Model.MAZDA2, Model.MAZDA5, Model.CX30, Model.MAZDA_CX50);

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
        CR_V,
        FIT,
        ODYSSEY,
        PILOT,
        RIDGELINE,
        CITY,
        BRIO,
        AMZE,
        //TOYOTA
        CAMRY,
        COROLLA,
        RAV4,
        PRIUS,
        YARIS,
        HIGHLANDER,
        TACOMA,
        SIENNA,
        AVALON,
        SUPRA,
        //FORD
        FOCUS,
        FIESTA,
        FUSION,
        MUSTANG,
        ESCAPE,
        EDGE,
        EXPLORER,
        RANGER,
        EXPEDITION,
        TAURUS,
        //CHEVRO
        CRUZE,
        MALIBU,
        IMPALA,
        CAMARO,
        EQUINOX,
        TRAVERSE,
        COLORADO,
        SILVERADO,
        SUBURBAN,
        TAHOE,
        //NISSAN
        ALTIMA,
        MAXIMA,
        ROGUE,
        SENTRA,
        VERSA,
        MURANO,
        PATHFINDER,
        TITAN,
        ARMADA,
        KICKS,
        //BMW
        X1,
        X3,
        X5,
        X6,
        Z4,
        I3,
        I8,
        //MERCEDES_BENZ
        C_CLASS,
        E_CLASS,
        S_CLASS,
        A_CLASS,
        CLASSE_GLA,
        GLE_CLASS,
        SLC_CLASS,
        GLK_CLASS,
        GL_CLASS,
        SLS_CLASS,
        //AUDI
        A3,
        A4,
        A5,
        A6,
        A7,
        A8,
        Q3,
        Q5,
        Q7,
        R8,
        //LEXUS
        ES,
        IS,
        LC,
        LS,
        LX,
        NX,
        RC,
        RX,
        UX,
        GX,
        //MAZDA
        MAZDA3,
        MAZDA6,
        MX5_MIATA,
        CX3,
        CX5,
        CX9,
        MAZDA2,
        MAZDA5,
        CX30,
        MAZDA_CX50
    }

}
