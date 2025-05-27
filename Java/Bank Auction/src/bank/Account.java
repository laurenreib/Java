package bank;

import java.util.HashMap;

/**
 * Account is a record, can't be overwritten, it will just
 * update manually & recreate the account with correct
 * amount
 */

public record Account(String User, double Account, int ID, int Type, HashMap<Integer, Double> Holds) {
}

