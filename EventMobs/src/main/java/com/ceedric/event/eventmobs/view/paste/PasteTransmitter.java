package com.ceedric.event.eventmobs.view.paste;

import java.io.IOException;
import java.io.InputStream;

public interface PasteTransmitter {


    public String send(InputStream stream) throws IOException;
}
