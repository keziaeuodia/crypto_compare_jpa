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

    /**
     * Method called by the controller to make a third party API call to crypto compare
     * @param param histoday or histohour
     * @param fsym fromCurrency passed by user
     * @param tsym toCurrency passed by user
     * @param limit limit of the data taken, max 2000
     * @param persist if persist is true, saveAllData method will be called to persist data to the database
     * @return
     */
    public CryptoRoot search(String param, String fsym, String tsym, int limit, boolean persist) {
        String fquery = "https://min-api.cryptocompare.com/data/"+param+"?fsym="+fsym+"&tsym="+tsym+"&limit="+limit;

        //mapping the data to the class
        CryptoRoot data = restTemplate.getForObject(fquery, CryptoRoot.class);

        //persisting data to DB
        if (persist){
            saveAllDataPerMinute(data, fsym, tsym, param);
        }
        return data;
    }

    /**
     * Method that's being called by the search method when persist value is true
     * This method calls the checkDuplicate method that will persist data to the database, only if there's no duplicate
     * @param data data retrieved from the search result
     * @param fsym fromCurrency
     * @param tsym toCurrency
     * @param param histominute or histohour
     */
    private void saveAllDataPerMinute(CryptoRoot data, String fsym, String tsym, String param){

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
            if (checkDuplicate(obj, param) == false)
                //write data to DB if there's no duplicate
                switch (param){
                    case "histominute":
                        cryptoMapper.saveCryptoData(obj);
                        break;
                    case "histohour" :
                        cryptoMapper.saveHourData(obj);
                }

        }
    }

    /**
     * checking if there is any duplicate time in the data based on time, fromCurrency, toCurrency, and timesignal
     * @param obj is an History object which will be compared against the data in the database
     * @return boolean value true if the data doesn't exist in the database
     */

    private boolean checkDuplicate (History obj, String param){
        History history;
        if(param.equals("histominute")) {
            history = cryptoMapper.getUniqueData(obj.getFromCurrency(), obj.getToCurrency(), obj.getTime());
            if (history == null) {
                return false;
            }else return true;
        }
        if (param.equals("histohour")){
            history = cryptoMapper.getHourUniqueData(obj.getFromCurrency(), obj.getToCurrency(), obj.getTime());
            if (history == null) {
                return false;
            }else return true;
        }
        return true;
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
