package com.t212.cfdaccounts.cfdaccounts.api.websocket.models;

import java.io.Serializable;

public class ClientInstrumentPage implements Serializable {
    private Integer page;

    public ClientInstrumentPage() {
    }

    public ClientInstrumentPage(Integer page) {
        this.page = page;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }
}
