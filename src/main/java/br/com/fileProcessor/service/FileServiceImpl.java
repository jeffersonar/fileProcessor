package br.com.fileProcessor.service;

import br.com.FileProcessor.service.FileService;
import br.com.fileProcessor.vo.ClientVo;
import br.com.fileProcessor.vo.FileOut;
import br.com.fileProcessor.vo.FileVo;
import br.com.fileProcessor.vo.SaleVo;
import br.com.fileProcessor.vo.SalesmanVo;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author jefferson
 */
@Service
public class FileServiceImpl implements FileService {

    private final String pathOut = "/data/out/";

    private final String pathIn = File.separator.concat("data")
                                                .concat(File.separator)
                                                .concat("in")
                                                .concat(File.separator);

    @Autowired
    private FileServiceImpl fileServiceImpl;

    @Override
    public String getInPath() {
        return this.getPathBase().concat(this.pathIn);
    }

    @Override
    public String getOutPath() {
        return this.getPathBase().concat(this.pathOut);
    }

    @Override
    public String getPathBase() {
        String homePath = System.getenv("HOMEPATH");
        homePath= System.getProperty("os.name").equals("Linux") ? homePath : pathWindows(homePath) ;
        return homePath != null ? homePath : "";
    }

    public void listArquivos(Queue<FileVo> queues) {
        File folderPath = new File(fileServiceImpl.getInPath());
        File[] fileList = folderPath.listFiles();
        if (fileList == null) {
            return;
        }
        List<FileVo> retorno = Arrays.asList(fileList).stream()
                .filter(file -> !queues.contains(file.getAbsolutePath()) && file.getAbsolutePath().endsWith(".dat"))
                .map(file -> new FileVo(file))
                .collect(Collectors.toList());
        queues.addAll(retorno);
    }

    public void processFiles(Queue<FileVo> fila) throws FileNotFoundException, IOException {
        FileVo path = fila.poll();
        if (path == null) {
            return;
        }
        path.getFile().renameTo(new File(path.getFile().getAbsolutePath().concat(".tmp")));
        BufferedReader file = new BufferedReader(new FileReader(path.getFile().getAbsolutePath().concat(".tmp"),StandardCharsets.UTF_8));
        List<SalesmanVo> salesman = new ArrayList<SalesmanVo>();
        List<ClientVo> client = new ArrayList<ClientVo>();
        List<SaleVo> sales = new ArrayList<SaleVo>();
        while (file.ready()) {
            String[] line = file.readLine().split("ç");
            switch (line[0]) {
                case "001":
                    salesman.add(new SalesmanVo(line));
                    break;
                case "002":
                    client.add(new ClientVo(line));
                    break;
                case "003":
                    sales.add(new SaleVo(line));
            }
        }
        file.close();
        FileOut outFile = outputDataGenerator(salesman, client, sales);
        generatorFile(path.getFile(), outFile);

        new File(path.getFile().getAbsolutePath().concat(".tmp")).delete();
    }

    private FileOut outputDataGenerator(List<SalesmanVo> salesman, List<ClientVo> client, List<SaleVo> sales) {
        FileOut fileOut = new FileOut();
        fileOut.setQtdVendedor(salesman.size());
        fileOut.setQtdclient(client.size());
        fileOut.setIdVendaCara(highestPriceSale(sales));
        fileOut.setPiorVendedor(worstSeller(sales));
        return fileOut;
    }

    private Integer highestPriceSale(List<SaleVo> sales) {
        return Integer.parseInt(sales.stream().max((x, y) -> x.getValorVenda().compareTo(y.getValorVenda())).get().getVendaId());
    }

    private String worstSeller(List<SaleVo> sales) {
        return sales.stream().min((x, y) -> x.getValorVenda().compareTo(y.getValorVenda())).get().getNomeVendedor();
    }

    private void generatorFile(File file, FileOut outputFile) throws IOException {
        File fileText = new File(this.getOutPath().concat(file.getName().replace(".dat", ""))
                .concat(".done.dat"));
        if (!new File(this.getOutPath()).exists()) {
            new File(this.getOutPath()).mkdirs();
        }
        if (!fileText.exists()) {
            fileText.createNewFile();
        }
        FileWriter fw = new FileWriter(fileText.getAbsolutePath());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(outputFile.toString());
        bw.close();

    }

    private String pathWindows(String homePath) {
        return new String("C:").concat(File.separator).concat(homePath);
    }

}
