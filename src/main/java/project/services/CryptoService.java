package project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import project.mappers.CryptoMapper;
import project.models.CryptoRoot;
import project.models.History;

import java.util.ArrayList;

@Service
public class CryptoService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    CryptoMapper cryptoMapper;


    public CryptoRoot search(String fsym, String tsym, boolean persist) {
        String fquery = "https://min-api.cryptocompare.com/data/histominute?fsym="+fsym+"&tsym="+tsym;

        //mapping the data to the class
        CryptoRoot data = restTemplate.getForObject(fquery, CryptoRoot.class);

        //persisting data to DB
        if (persist){
            saveAllDataPerMinute(data, fsym, tsym);
        }
        return data;
    }

    //saving one history data (at the index of 0) into the DB
    private void saveCryptoData(CryptoRoot data, String fsym, String tsym) {

        History obj = new History();

        obj.setFromCurrency(fsym);
        obj.setToCurrency(tsym);
        obj.setTime(data.getData()[0].getTime());
        obj.setClose(data.getData()[0].getClose());
        obj.setHigh(data.getData()[0].getHigh());
        obj.setLow(data.getData()[0].getLow());
        obj.setOpen(data.getData()[0].getOpen());
        obj.setVolumefrom(data.getData()[0].getVolumefrom());
        obj.setVolumeto(data.getData()[0].getVolumeto());

        cryptoMapper.saveCryptoData(obj);

    }

    //saving the history data to DB
    private void saveAllDataPerMinute(CryptoRoot data, String fsym, String tsym){

        History obj = new History();

        //loop through the data object and set it to DB
        for(int i = 0; i < data.getData().length; i++) {

            obj.setFromCurrency(fsym);
            obj.setToCurrency(tsym);
            obj.setTime(data.getData()[i].getTime());
            obj.setClose(data.getData()[i].getClose());
            obj.setHigh(data.getData()[i].getHigh());
            obj.setLow(data.getData()[i].getLow());
            obj.setOpen(data.getData()[i].getOpen());
            obj.setVolumefrom(data.getData()[i].getVolumefrom());
            obj.setVolumeto(data.getData()[i].getVolumeto());

            //calling the method that checks duplicate
            if (checkDuplicate(obj) == false)
                //write data to DB if there's no duplicate
                cryptoMapper.saveCryptoData(obj);
        }
    }

    //checking if there is any duplicate time in the data based on fromCurrency, toCurrency, and time
    private boolean checkDuplicate (History obj){
        History history = cryptoMapper.getUniqueData(obj.getFromCurrency(), obj.getToCurrency(), obj.getTime());
        if (history == null) {
            return false;
        }else return true;
    }

    public ArrayList<History> getAllData(){
        return cryptoMapper.getAllData();
    }

    public ArrayList<History> getDataByFsym(String fsym){
        return cryptoMapper.getDataByFsym(fsym);
    }

    public ArrayList<History> getDataByTsym(String tsym) {
        return cryptoMapper.getDataByTsym(tsym);
    }

    public String addData(History data) {
        cryptoMapper.saveCryptoData(data);
        return "Data inserted";
    }

    public String deleteDataById(int id) {
        cryptoMapper.deleteDataById(id);
        return "Data id: " + id + " deleted.";
    }

    public History update(History data) {
        cryptoMapper.update(data);
        return cryptoMapper.getDataById(data.getId());
    }

    public History getDataById(int id) {
        return cryptoMapper.getDataById(id);
    }


}
