package Service;

import DB.DBConnection;
import Utils.Constants;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class PortfolioService {

    public static void processRequest(String path) throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path));

            String line = br.readLine();

            addAllocation(line);
            addSIP(br.readLine());

            line = br.readLine();

            boolean isSIP = false;
            while (line != null) {

                if (line.contains("REBALANCE")) {
                    printRebalance();
                } else if (line.contains("BALANCE")) {
                    printBalance(line);
                } else {
                    updateChanges(line, isSIP);
                    isSIP = true;
                }
                line = br.readLine();
            }

        } catch (FileNotFoundException e) {
            System.err.println("File not found");
        } catch (java.io.IOException e) {
            System.err.println("Exception reading file");
        } finally {
            br.close();
        }
    }

    private static String[] parseLine(String in) {
        return in.split(Constants.SPLITTER);
    }

    private static void updateChanges(String input, boolean isSIP) {
        String[] arr = parseLine(input);

        DBConnection dbConnection = DBConnection.open();
        List<Integer> currentValue = dbConnection.getPortfolioBalance();

        int equity = currentValue.get(0);
        int debt = currentValue.get(1);
        int gold = currentValue.get(2);
        String month = arr[4];

        if (isSIP) {
            List<Integer> SIPValue = dbConnection.getSIPValues();

            equity = equity + SIPValue.get(0);
            debt = debt + SIPValue.get(1);
            gold = gold + SIPValue.get(2);
        }

        equity = (int) (equity + Math.floor(equity * Float.parseFloat(arr[1].replace("%", "")) / 100));
        debt = (int) (debt + Math.floor(debt * Float.parseFloat(arr[2].replace("%", "")) / 100));
        gold = (int) (gold + Math.floor(gold * Float.parseFloat(arr[3].replace("%", "")) / 100));

//        System.out.println("equity = " + equity + " debt = " + debt + " gold = " + gold);

        dbConnection.addAllocation(equity, debt, gold);
        dbConnection.updateChange(month, equity, debt, gold);
        if (month.contains(Constants.JUNE_MONTH) || month.contains(Constants.DEC_MONTH)) {


        }
    }

    private static boolean addAllocation(String input) {
        String[] arr = parseLine(input);
        if (arr[0].contains(Constants.ALLOCATE)) {
            int equity = Integer.parseInt(arr[1]);
            int debt = Integer.parseInt(arr[2]);
            int gold = Integer.parseInt(arr[3]);

            int sum = equity + debt + gold;

            float equityPer = (float) Math.floor(equity * 100 / sum);
            float debtPer = (float) Math.floor(debt * 100 / sum);
            float goldyPer = (float) Math.floor(gold * 100 / sum);


            DBConnection dbConnection = DBConnection.open();
            dbConnection.addAllocation(equity, debt, gold);
            dbConnection.updatePortfolioPercentage(equityPer, debtPer, goldyPer);

        } else {
            return false;
        }
        return true;
    }

    private static boolean addSIP(String input) {
        String[] arr = parseLine(input);
        if (arr[0].contains(Constants.SIP)) {
            int equity = Integer.parseInt(arr[1]);
            int debt = Integer.parseInt(arr[2]);
            int gold = Integer.parseInt(arr[3]);

            DBConnection dbConnection = DBConnection.open();
            dbConnection.updateSIP(equity, debt, gold);

        } else {
            return false;
        }
        return true;
    }

    public static void printBalance(String line) {
        String[] arr = parseLine(line);
        DBConnection dbConnection = DBConnection.open();
        List<Integer> li = dbConnection.getMonthBalance(arr[1]);
        System.out.println(li.get(0) + " " + li.get(1) + " " + li.get(2));
    }

    private static void doRebalance(DBConnection dbConnection, String month) {
        List<Integer> currentValue = dbConnection.getMonthBalance(month);
        int equity = currentValue.get(0);
        int debt = currentValue.get(1);
        int gold = currentValue.get(2);

        int sum = equity + debt + gold;

        float equityPer = dbConnection.getinitalpercentage().get(Constants.EQUITY_PERCENTAGE);
        float debtPer = dbConnection.getinitalpercentage().get(Constants.DEBT_PERCENTAGE);
        float goldyPer = dbConnection.getinitalpercentage().get(Constants.GOLD_PERCENTAGE);

        equity = (int) (Math.floor(sum * equityPer / 100));
        debt = (int) (Math.floor(sum * debtPer / 100));
        gold = (int) (Math.floor(sum * goldyPer / 100));

        dbConnection.updateChange(month, equity, debt, gold);
        dbConnection.addAllocation(equity, debt, gold);

        System.out.println(equity + " " + debt + " " + gold);
    }

    public static void printRebalance() {
        DBConnection dbConnection = DBConnection.open();
        Set<String> moths = dbConnection.getMonths();
        if (!moths.contains(Constants.JUNE_MONTH) && !moths.contains(Constants.DEC_MONTH)) {
            System.out.println("CANNOT_REBALANCE");
        } else {
            for (String str : moths) {
                if (str.equalsIgnoreCase(Constants.JUNE_MONTH) | str.equalsIgnoreCase(Constants.DEC_MONTH)) {
                    doRebalance(dbConnection, str);
                    break;
                }
            }
        }
    }
}
