package com.test.hilu0318.bluetoothsender.domain;

/**
 * Created by hilu0 on 2017-12-04.
 */

public class MusicInfo {

    private String filepath;
    private String filename;
    private int filesize;
    private String mimetype;

    public void setFilepath(String filepath){ this.filepath = filepath; }
    public void setFilename(String filename){ this.filename = filename; }
    public void setFilesize(int filesize){ this.filesize = filesize; }
    public void setMimetype(String mimetype){ this.mimetype = mimetype; }

    public String getFilepath(){ return this.filepath; }
    public String getFilename(){ return this.filename; }
    public int getFilesize(){ return this.filesize; }
    public String getMimetype(){ return this.mimetype; }
}
