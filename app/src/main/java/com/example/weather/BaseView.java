package com.example.weather;

public interface BaseView<T> {
    //view should have reference of presenter,
    //this method should be call when presenter has been created.
    void setPresenter(T presenter);
}
