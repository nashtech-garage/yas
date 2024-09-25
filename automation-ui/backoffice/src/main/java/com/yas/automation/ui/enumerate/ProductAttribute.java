package com.yas.automation.ui.enumerate;

public enum ProductAttribute {
    CPU,
    GPU,
    OS,
    SCREEN_SIZE,
    PANEL,
    BLUETOOTH,
    NFC,
    MAIN_CAMERA,
    SUB_CAMERA,
    RAM,
    STORAGE,
    SCREEN_RESOLUTION;

    public static final String BASE_XPATH = "//*[@class=\"fade tab-pane active show\"]/table/tbody/tr[%s]/th[2]/input";
}
