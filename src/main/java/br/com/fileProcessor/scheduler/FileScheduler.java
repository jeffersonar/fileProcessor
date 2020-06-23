package br.com.fileProcessor.scheduler;

import br.com.fileProcessor.vo.FileVo;
import br.com.fileProcessor.service.FileServiceImpl;
import java.util.LinkedList;
import java.util.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author jefferson
 */
@Component
@EnableScheduling
public class FileScheduler {

    private static final Logger log = LoggerFactory.getLogger(FileScheduler.class);
    private static Queue<FileVo> fila = new LinkedList();

    @Autowired
    private FileServiceImpl fileServiceImpl;

    @Scheduled(fixedRate = 5000)
    public void collectFile() {
        try {
            fileServiceImpl.listArquivos(fila);
        } catch (Exception error) {
            log.error("collectFile", error);
        }
    }

   @Scheduled(fixedRate = 5000)
    public void processFile() {
        try {
            fileServiceImpl.processFiles(fila);
        } catch (Exception error) {
            log.error("processFile", error);
        }
    }
}
