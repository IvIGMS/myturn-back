package com.ivanfrias.myturn.notifications.dao.models.enums;

public enum NotificationTypeEnum {
    END_SUBSCRIPTION("END_SUBSCRIPTION");

    private final String role;

    NotificationTypeEnum(String role) {
        this.role = role;
    }

    public String getValue() {
        return role;
    }
}
