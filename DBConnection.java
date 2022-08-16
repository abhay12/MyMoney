package DB;

import Utils.Constants;

import java.util.*;

public class DBConnection {

    //    HashMap<String, Integer> sip = new HashMap<>();
    HashMap<String, Integer> database = new HashMap<>();


    HashMap<String, Float> initalpercentage = new HashMap<>();

    LinkedHashMap<String, HashMap<String, Integer>> finalData = new LinkedHashMap<>();

    static DBConnection dbConnection;

    private DBConnection() {

    }

    public HashMap<String, Float> getinitalpercentage() {
        return initalpercentage;
    }


    public static DBConnection open() {
        if (dbConnection == null) {
            dbConnection = new DBConnection();
        }
        return dbConnection;
    }

    public Set<String> getMonths() {
        return finalData.keySet();
    }

    public List<Integer> getMonthBalance(String month) {
        List<Integer> result = new ArrayList<>();
        HashMap<String, Integer> database = finalData.get(month);
        result.add(database.get(Constants.EQUITY_BALANCE_KEY));
        result.add(database.get(Constants.DEBT_BALANCE_KEY));
        result.add(database.get(Constants.GOLD_BALANCE_KEY));

        return result;
    }

    public void updateChange(String month, int equity, int debt, int gold) {
        addAllocation(equity, debt, gold);

        HashMap<String, Integer> hm = new HashMap<>();
        hm.put(Constants.EQUITY_BALANCE_KEY, equity);
        hm.put(Constants.DEBT_BALANCE_KEY, debt);
        hm.put(Constants.GOLD_BALANCE_KEY, gold);
        finalData.put(month, hm);
    }

    public List<Integer> getPortfolioBalance() {
        List<Integer> result = new ArrayList<>();
        result.add(database.get(Constants.EQUITY_BALANCE_KEY));
        result.add(database.get(Constants.DEBT_BALANCE_KEY));
        result.add(database.get(Constants.GOLD_BALANCE_KEY));

        return result;
    }

    public List<Integer> getSIPValues() {
        List<Integer> result = new ArrayList<>();
        result.add(database.get(Constants.EQUITY_SIP_KEY));
        result.add(database.get(Constants.DEBT_SIP_KEY));
        result.add(database.get(Constants.GOLD_SIP_KEY));

        return result;
    }

    public void updateSIP(int equity, int debt, int gold) {
        database.put(Constants.EQUITY_SIP_KEY, equity);
        database.put(Constants.DEBT_SIP_KEY, debt);
        database.put(Constants.GOLD_SIP_KEY, gold);
    }

    public void addAllocation(int equity, int debt, int gold) {
        database.put(Constants.EQUITY_BALANCE_KEY, equity);
        database.put(Constants.DEBT_BALANCE_KEY, debt);
        database.put(Constants.GOLD_BALANCE_KEY, gold);
    }

    public void updatePortfolioPercentage(Float equity, Float debt, Float gold) {
        initalpercentage.put(Constants.EQUITY_PERCENTAGE, equity);
        initalpercentage.put(Constants.DEBT_PERCENTAGE, debt);
        initalpercentage.put(Constants.GOLD_PERCENTAGE, gold);

    }
}
