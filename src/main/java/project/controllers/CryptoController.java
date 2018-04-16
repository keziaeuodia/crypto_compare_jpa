package project.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import project.models.CryptoRoot;
import project.models.History;
import project.services.CryptoService;

import java.util.ArrayList;

@RestController
@RequestMapping("/data")

public class CryptoController {

    @Autowired
    CryptoService cryptoService;

    /**
     * Method called by the controller to make a third party API call to crypto compare
     * e.g. /data/histominute?fsym=BTC&tsym=USD&limit=365&persist=true
     * @param fsym fromCurrency passed by user
     * @param tsym toCurrency passed by user
     * @param limit limit of the data taken, max 2000
     * @param persist if persist is true, saveAllData method will be called to persist data to the database
     * @return
     */
    @RequestMapping("/histominute")
    public CryptoRoot search (@RequestParam(value = "fsym", required = true) String fsym,
                              @RequestParam(value = "tsym", required = true) String tsym,
                              @RequestParam(value = "limit", required = false, defaultValue = "1440") int limit,
                              @RequestParam(value = "persist", defaultValue = "false") boolean persist){
        return cryptoService.search("histominute", fsym, tsym, limit, persist);
    }

    /**
     * Method called by the controller to make a third party API call to crypto compare
     * e.g. /data/histohour?fsym=BTC&tsym=USD&limit=365&persist=true
     * @param fsym fromCurrency passed by user
     * @param tsym toCurrency passed by user
     * @param limit limit of the data taken, max 2000
     * @param persist if persist is true, saveAllData method will be called to persist data to the database
     * @return
     */
    @RequestMapping("/histohour")
    public CryptoRoot searchHour (@RequestParam(value = "fsym", required = true) String fsym,
                              @RequestParam(value = "tsym", required = true) String tsym,
                              @RequestParam(value = "limit", required = false, defaultValue = "500") int limit,
                              @RequestParam(value = "persist", defaultValue = "false") boolean persist){
        return cryptoService.search("histohour", fsym, tsym, limit, persist);
    }

    /**
     * getting history of crypto data from the database
     * path: /data/tsym?tsym=USD
     * @param fsym determine cryptocurrency you want to query e.g BTC
     * @return list of History data where toCurrency = fsym
     */
    @RequestMapping(method= RequestMethod.GET, value = "/fsym")
    public ArrayList<History> findByFsym(@RequestParam(value = "fsym", required = true) String fsym){
        return cryptoService.getDataByFsym(fsym);
    }

    /**
     * getting history of crypto data from the database
     * e.g. /data/tsym?tsym=USD
     * @param tsym determine the currency to compare the fsym against e.g USD
     * @return list of History data where toCurrency = tsym
     */
    @RequestMapping(method= RequestMethod.GET, value = "/tsym")
    public ArrayList<History> findByTsym(@RequestParam(value = "tsym", required = true) String tsym){
        return cryptoService.getDataByTsym(tsym);
    }

    /**
     * getting history of crypto data from the database
     * e.g. /data/1290
     * @param id the id of the data requested
     * @return a history data of the id on the query
     */
    @GetMapping("/{id}")
    public History getDataById(@PathVariable(value = "id") int id){
        return cryptoService.getDataById(id);
    }

    /**
     * getting all history data from the histominute table in the database
     * e.g. /data/
     * @return list of histominute data
     */
    @GetMapping("/")
    public ArrayList<History> findAll(){
        return cryptoService.getAllData();
    }

    /**
     * using curl or postman, insert a new data object
     * e.g. /data/
     * @param data
     * @return
     */
    @PostMapping("/")
    public String add(@RequestBody History data){
        return cryptoService.addData(data);
    }

    /**
     * using curl or postman patch a history data of the primary key (id)
     * e.g. /data/
     * @param data
     * @return
     */
    @PatchMapping("/")
    public History update(@RequestBody History data) {
        return cryptoService.update(data);
    }

    /**
     * using curl or postman delete a history data based on id
     * e.g. /data/879
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable(value = "id") int id){
        return cryptoService.deleteDataById(id);
    }

}
