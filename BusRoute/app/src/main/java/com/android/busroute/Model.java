package com.android.busroute;
public class Model {

    private String title;
    private String routes;
    private String duration;

    public Model(String title, String routes, String duration) {
        this.routes = routes;
        this.title = title;
        this.duration = duration;
    }

    public String getRoute() {
        return routes;
    }

    public void setRoute(String routes) {
        this.routes = routes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
