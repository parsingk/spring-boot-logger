package com.spring.boot.logger.aws;

import lombok.Getter;

public enum AwsKinesisLogType {

    BOTH("both", -1),
    APPLICATION("application", 0),
    GENERAL("general", 1);


    @Getter
    private String label;
    @Getter
    private int type;

    AwsKinesisLogType(String label, int type) {
        this.label = label;
        this.type = type;
    }

    public static AwsKinesisLogType getTypeByLabel(String str) {
        if (str.equalsIgnoreCase(AwsKinesisLogType.APPLICATION.getLabel())) {
            return AwsKinesisLogType.APPLICATION;
        } else if (str.equalsIgnoreCase(AwsKinesisLogType.GENERAL.getLabel())) {
            return AwsKinesisLogType.GENERAL;
        } else {
            return AwsKinesisLogType.BOTH;
        }
    }

    public static AwsKinesisLogType getTypeByType(int type) {
        if (type == AwsKinesisLogType.APPLICATION.getType()) {
            return AwsKinesisLogType.APPLICATION;
        } else if (type == AwsKinesisLogType.GENERAL.getType()) {
            return AwsKinesisLogType.GENERAL;
        } else {
            return AwsKinesisLogType.BOTH;
        }
    }
}
