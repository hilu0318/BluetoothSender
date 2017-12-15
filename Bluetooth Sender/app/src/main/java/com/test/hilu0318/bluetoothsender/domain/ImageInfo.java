package com.test.hilu0318.bluetoothsender.domain;

/**
 * Created by hilu0 on 2017-11-27.
 */

public class ImageInfo {
    private boolean check;
    private String filename;
    private String filepath;
    private int filesize;
    private int orientation;
    private String mimetype;

    public void setCheck(boolean check) { this.check = check; }
    public void setFilename(String filename) { this.filename = filename; }
    public void setFilepath(String filepath) { this.filepath = filepath; }
    public void setFilesize(int filesize) { this.filesize = filesize; }
    public void setOrientation(int orientation) { this.orientation = orientation; }
    public void setMimetype(String mimetype){ this.mimetype = mimetype; }

    public boolean getCheck() { return this.check; }
    public String getFilename(){ return this.filename; }
    public String getFilepath(){ return this.filepath; }
    public int getFilesize(){ return this.filesize; }
    public int getOrientation(){ return this.orientation; }
    public String getMimetype(){ return this.mimetype; }
}
