package com.ceedric.eventkoth.view.paste;

import java.io.IOException;
import java.io.InputStream;

public interface PasteTransmitter {

    String send(InputStream stream) throws IOException;

}

