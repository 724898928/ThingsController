package com.lixin.gameInterface;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by li on 2018/5/23.
 */

public interface FileIO {

    public InputStream readAsset(String fileName) throws IOException;
    public OutputStream writeFile(String fileName) throws IOException;
    public InputStream readFile(String fileName) throws IOException;

}
