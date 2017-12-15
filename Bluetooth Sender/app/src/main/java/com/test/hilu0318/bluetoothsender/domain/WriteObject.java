package com.test.hilu0318.bluetoothsender.domain;

import java.io.Serializable;

/**
 * Created by hilu0 on 2017-12-04.
 */

public class WriteObject implements Serializable {
    private String filename;
    private byte[] data;
    private int filesize;
    private int start = 1;
    private String mimetype;

    public void setFilename(String filename){ this.filename = filename; }
    public void setFilesize(int filesize){ this.filesize = filesize; }
    public void setData(byte[] data){ this.data = data; }
    public void setStart(int start){ this.start = start; }
    public void setMimetype(String mimetype){ this.mimetype = mimetype; }

    public String getFilename(){ return this.filename; }
    public int getFilesize(){ return this.filesize; }
    public byte[] getData(){ return this.data; }
    public int getStart(){ return this.start; }
    public String getMimetype(){ return this.mimetype; }

    public String toString(){
        return "WriteObject [filename="+this.filename+", filesize="+this.filesize+", DataLength="+this.data.length+"]";
    }
}
