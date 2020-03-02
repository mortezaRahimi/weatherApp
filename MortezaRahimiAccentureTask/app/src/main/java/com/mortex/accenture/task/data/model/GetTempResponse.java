package com.mortex.accenture.task.data.model;

public class GetTempResponse {

    private Temperature main;


    private Integer id;

    public Integer getId() {
        return id;
    }

    private long dt;
    private String name;

    public Temperature getMain() {
        return main;
    }

    public long getDt() {
        return dt;
    }

    public String getName() {
        return name;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public GetTempResponse(Temperature main, long dt , String name) {
        this.main = main;
        this.dt = dt;
        this.name = name;
    }

    public GetTempResponse(Temperature main, int id, long dt, String name) {
        this.main = main;
        this.id = id;
        this.dt = dt;
        this.name = name;
    }
}
