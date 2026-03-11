package com.krishiYatra.krishiYatra.buyer;

public class BuyerConst {
    public static final String USER_NOT_AUTHENTICATED = "User not authenticated";
    public static final String USER_NOT_FOUND = "User not found.";
    public static final String ALREADY_BUYER = "You have already registered as a buyer";
    public static final String REGISTRATION_SUCCESS = "Registered for buyer successfully. Waiting for admin verification.";
    
    public static final String REGISTRATION_NOT_FOUND = "Buyer registration not found";
    public static final String VERIFICATION_SUCCESS = "Buyer verified successfully";
    public static final String REJECTION_PREFIX = "Buyer registration rejected. Reason: ";
    
    public static final String DASHBOARD_WELCOME = "Welcome to the Buyer Dashboard";

    private BuyerConst() {
        // Private constructor to prevent instantiation
    }
}
