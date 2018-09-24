package com.pp.toptal.soccermanager.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.nio.charset.Charset;

public class UnicodeReader extends Reader {
    private static final int BOM_SIZE = 4;
    private final InputStreamReader reader;

    public UnicodeReader(InputStream in, Charset defaultEncoding) throws IOException {
        byte bom[] = new byte[BOM_SIZE];
        Charset encoding;
        int unread;
        PushbackInputStream pushbackStream = new PushbackInputStream(in, BOM_SIZE);
        int n = pushbackStream.read(bom, 0, bom.length);

        // Read ahead four bytes and check for BOM marks.
        if ((bom[0] == (byte) 0xEF) && (bom[1] == (byte) 0xBB) && (bom[2] == (byte) 0xBF)) {
            encoding = Charset.forName("UTF-8");
            unread = n - 3;
        } else if ((bom[0] == (byte) 0xFE) && (bom[1] == (byte) 0xFF)) {
            encoding = Charset.forName("UTF-16BE");
            unread = n - 2;
        } else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE)) {
            encoding = Charset.forName("UTF-16LE");
            unread = n - 2;
        } else if ((bom[0] == (byte) 0x00) && (bom[1] == (byte) 0x00) && (bom[2] == (byte) 0xFE) && (bom[3] == (byte) 0xFF)) {
            encoding = Charset.forName("UTF-32BE");
            unread = n - 4;
        } else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE) && (bom[2] == (byte) 0x00) && (bom[3] == (byte) 0x00)) {
            encoding = Charset.forName("UTF-32LE");
            unread = n - 4;
        } else {
            encoding = defaultEncoding;
            unread = n;
        }

        // Unread bytes if necessary and skip BOM marks.
        if (unread > 0) {
            pushbackStream.unread(bom, (n - unread), unread);
        } else if (unread < -1) {
            pushbackStream.unread(bom, 0, 0);
        }

        // Use given encoding.
        if (encoding == null) {
            reader = new InputStreamReader(pushbackStream);
        } else {
            reader = new InputStreamReader(pushbackStream, encoding);
        }
    }

    public String getEncoding() {
        return reader.getEncoding();
    }
    
    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        return reader.read(cbuf, off, len);
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
