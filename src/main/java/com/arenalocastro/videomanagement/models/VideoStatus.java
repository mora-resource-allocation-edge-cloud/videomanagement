package com.arenalocastro.videomanagement.models;

import java.io.Serializable;

public enum VideoStatus implements Serializable {
    WaitingUpload,
    Uploaded,
    Available,
    NotAvailable,
    AvailableWithOnlineEncoding
}
