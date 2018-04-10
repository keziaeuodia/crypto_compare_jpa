package project.mappers;

import org.apache.ibatis.annotations.*;
import project.models.History;

import java.util.ArrayList;

@Mapper
public interface CryptoMapper {

    String INSERT_DATA = "INSERT INTO `crypto-project`.`histoMinute`(`id`,`fromCurrency`,`toCurrency`,`time`,`close`,`open`,`high`,`low`,`volumefrom`,`volumeto`)"+
            " VALUES (#{id},#{fromCurrency},#{toCurrency},#{time},#{close},#{open},#{high},#{low},#{volumefrom},#{volumeto});";
    String GET_ALL_DATA = "SELECT * FROM `crypto-project`.`histoMinute`;";
    String GET_DATA_BY_FSYM = "SELECT * FROM `crypto-project`.`histoMinute` WHERE `fromCurrency` = #{fromCurrency};";
    String GET_DATA_BY_TSYM = "SELECT * FROM `crypto-project`.`histoMinute` WHERE `toCurrency` = #{toCurrency};";
    String GET_DATA_BY_ID = "SELECT * FROM `crypto-project`.`histoMinute` WHERE `id` = #{id};";
    String DELETE_DATA_BY_ID = "DELETE FROM `crypto-project`.`histoMinute` WHERE `id` = #{id};";
    String UPDATE_DATA_BY_ID = "UPDATE `crypto-project`.`histoMinute`SET`id` = #{id},`fromCurrency` = #{fromCurrency}," +
            "`toCurrency` = #{toCurrency},`time` = #{time},`close` = #{close},`open` = #{open},`high` = #{high}," +
            "`low` = #{low},`volumeFrom` = #{volumeFrom},`volumeTo` = #{volumeTo}WHERE `id` = #{id};";
    String GET_UNIQUE_DATA = "SELECT * FROM `crypto-project`.`histoMinute` " +
            "WHERE `fromCurrency` = #{arg0} AND `toCurrency` = #{arg1} AND `time` = #{arg2};";

    @Insert(INSERT_DATA)
    public void saveCryptoData(History obj);

    @Select(GET_ALL_DATA)
    public ArrayList<History> getAllData();

    @Select(GET_DATA_BY_FSYM)
    public ArrayList<History> getDataByFsym(String fsym);

    @Select(GET_DATA_BY_TSYM)
    public ArrayList<History> getDataByTsym(String tsym);

    @Select(GET_UNIQUE_DATA)
    public History getUniqueData(String fsym, String tsym, long time);

    @Select(GET_DATA_BY_ID)
    History getDataById(int id);

    @Delete(DELETE_DATA_BY_ID)
    public void deleteDataById(int id);

    @Update(UPDATE_DATA_BY_ID)
    public int update(History history);

}
