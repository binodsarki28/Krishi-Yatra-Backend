package com.krishiYatra.krishiYatra.notification;

public class NotificationConst {
    public static final String TOKEN_SAVED = "FCM token saved successfully.";
    public static final String TOKEN_SAVE_FAILED = "Failed to save FCM token.";
    public static final String TEST_PUSH_SENT = "Test push sent successfully.";
    public static final String USER_NOT_FOUND = "User not found.";
    public static final String NOTIFICATION_SENT = "Notification sent successfully.";
    public static final String NOTIFICATION_FAILED = "Unable to process notification delivery.";
    public static final String NOTIFICATIONS_FETCHED = "Notifications fetched successfully.";
    public static final String NOTIFICATION_MARKED_READ = "Notification marked as read.";

    // Order Created Notification Templates
    public static final String ORDER_CREATED_FARMER_TITLE = "New Order Received!";
    public static final String ORDER_CREATED_FARMER_BODY = "You have received a new order for %s (ID: %s). Wait for the rider to accept.";
    public static final String ORDER_CREATED_DELIVERY_TITLE = "New Delivery Job Available!";
    public static final String ORDER_CREATED_DELIVERY_BODY = "A new order for %s is ready for pickup (ID: %s). Claim the job now!";
    public static final String ORDER_CREATED_BUYER_TITLE = "Order Confirmed!";
    public static final String ORDER_CREATED_BUYER_BODY = "Your order for %s (ID: %s) has been placed successfully. Wait for the rider to accept.";

    // Order accepted notification
}
