package com.krishiYatra.krishiYatra.delivery;

public class DeliveryConst {
    public static final String USER_NOT_AUTHENTICATED = "User not authenticated";
    public static final String USER_NOT_FOUND = "User not found.";
    public static final String ALREADY_DELIVERY = "You have already registered as a delivery partner";
    public static final String REGISTRATION_SUCCESS = "Registered for delivery partner successfully. Waiting for admin verification.";
    
    public static final String REGISTRATION_NOT_FOUND = "Delivery partner registration not found";
    public static final String VERIFICATION_SUCCESS = "Delivery partner verified successfully";
    public static final String REJECTION_PREFIX = "Delivery partner registration rejected. Reason: ";
    
    public static final String DASHBOARD_WELCOME = "Welcome to the Delivery Partner Dashboard";

    private DeliveryConst() {
        // Private constructor to prevent instantiation
    }
}
