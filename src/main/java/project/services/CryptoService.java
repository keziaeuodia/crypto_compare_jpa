package project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import project.mappers.CryptoMapper;
import project.models.CryptoRoot;
import project.models.HistoMinute;

import java.util.ArrayList;

@Service
public class CryptoService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    CryptoMapper cryptoMapper;


    public CryptoRoot search(String fsym, String tsym, boolean persist) {
        String fquery = "https://min-api.cryptocompare.com/data/histominute?fsym="+fsym+"&tsym="+tsym;

        CryptoRoot data = restTemplate.getForObject(fquery, CryptoRoot.class);

        if (persist){
            saveAllDataPerMinute(data, fsym, tsym);
        }
        return data;
    }

    private void saveAllDataPerMinute(CryptoRoot data, String fsym, String tsym){

        HistoMinute obj = new HistoMinute();

        for(int i = 0; i < data.getData().length; i++) {
            obj.setFromCurrency(fsym);
            obj.setToCurrency(tsym);
            obj.setTime(data.getData()[i].getTime());
            obj.setOpen(data.getData()[i].getOpen());
            obj.setHigh(data.getData()[i].getHigh());
            obj.setLow(data.getData()[i].getLow());
            obj.setClose(data.getData()[i].getClose());

            cryptoMapper.saveCryptoData(obj);
        }
    }

    public ArrayList<HistoMinute> getAllData(){
        return cryptoMapper.getAllData();
    }

    public ArrayList<HistoMinute> getDataByFsym(String fsym){
        return cryptoMapper.getDataByFsym(fsym);
    }

    public ArrayList<HistoMinute> getDataByTsym(String tsym) {
        return cryptoMapper.getDataByTsym(tsym);
    }

    public String addData(HistoMinute data) {
        cryptoMapper.saveCryptoData(data);
        return "Data inserted";
    }

    public String deleteDataById(int id) {
        cryptoMapper.deleteDataById(id);
        return "Data id: " + id + " deleted.";
    }

    public HistoMinute update(HistoMinute data) {
        cryptoMapper.update(data);
        return cryptoMapper.getDataById(data.getId());
    }

    public HistoMinute getDataById(int id) {
        return cryptoMapper.getDataById(id);
    }


}
