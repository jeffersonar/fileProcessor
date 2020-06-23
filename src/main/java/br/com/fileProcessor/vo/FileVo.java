package br.com.fileProcessor.vo;

import java.io.File;

/**
 *
 * @author jefferson
 */
public class FileVo {
    
    private File file;
    
    private boolean processado;

    public FileVo() {
    }
    
    public FileVo(File file){
        this.file=file;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }


    public boolean isProcessado() {
        return processado;
    }

    public void setProcessado(boolean processado) {
        this.processado = processado;
    }
    
}
