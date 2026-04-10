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
    public static final String NOTIFICATION_DELETED = "Notification deleted successfully.";

    // Frontend Route Constants
    public static final String TRACK_URL_BUYER = "/buyer/orders/track/%s";
    public static final String TRACK_URL_FARMER = "/farmer/orders/track/%s";
    public static final String TRACK_URL_DELIVERY = "/delivery/track/%s";
    
    public static final String EDIT_STOCK_URL = "/farmer/stocks/update/%s";
    public static final String AVAILABLE_JOBS_URL = "/delivery/jobs/available";

    // Demand URLs
    public static final String DEMAND_MARKET_URL = "/demands";
    public static final String BUYER_DEMANDS_URL = "/stocks";
    public static final String FARMER_STOCKS_URL = "/farmer/stocks/my-stocks";

    // Role-based Dashboards & Registration URLs
    public static final String URL_FARMER_DASHBOARD = "/farmer";
    public static final String URL_FARMER_REGISTER = "/farmer/register";
    public static final String URL_BUYER_DASHBOARD = "/buyer";
    public static final String URL_BUYER_REGISTER = "/buyer/register";
    public static final String URL_DELIVERY_DASHBOARD = "/delivery";
    public static final String URL_DELIVERY_REGISTER = "/delivery/register";

    // Order Created Notification Templates
    public static final String ORDER_CREATED_FARMER_TITLE = "New Order Received!";
    public static final String ORDER_CREATED_FARMER_BODY = "You have received a new order for %s (ID: %s). Wait for the rider to accept.";
    public static final String ORDER_CREATED_DELIVERY_TITLE = "New Delivery Job Available!";
    public static final String ORDER_CREATED_DELIVERY_BODY = "A new order for %s is ready for pickup (ID: %s). Claim the job now!";
    public static final String ORDER_CREATED_BUYER_TITLE = "Order Confirmed!";
    public static final String ORDER_CREATED_BUYER_BODY = "Your order for %s (ID: %s) has been placed successfully. Wait for the rider to accept.";

    // Order accepted notification
    public static final String ORDER_ACCEPTED_BUYER_TITLE = "Rider Assigned!";
    public static final String ORDER_ACCEPTED_BUYER_BODY = "A rider has claimed your order for %s (ID: %s). They will arrive for pickup soon.";
    public static final String ORDER_ACCEPTED_FARMER_TITLE = "Order Claimed!";
    public static final String ORDER_ACCEPTED_FARMER_BODY = "A rider has claimed the order for %s (ID: %s). Please ensure the stock is ready for pickup.";

    // Order picked up notification
    public static final String ORDER_PICKED_UP_BUYER_TITLE = "Order on the Way!";
    public static final String ORDER_PICKED_UP_BUYER_BODY = "Your order for %s (ID: %s) has been picked up and is now on the way!";

    // Order checkpoint notification
    public static final String ORDER_CHECKPOINT_TITLE = "Delivery Update!";
    public static final String ORDER_CHECKPOINT_BODY = "Rider has reached %s on your order of %s (ID: %s).";

    // Order status update notifications
    public static final String ORDER_DELIVERED_TITLE = "Order Delivered!";
    public static final String ORDER_DELIVERED_BODY = "Your order for %s (ID: %s) has been successfully delivered!";
    
    public static final String ORDER_CONFLICT_BUYER_TITLE = "Conflict Reported!";
    public static final String ORDER_CONFLICT_BUYER_BODY = "You reported a conflict for %s (ID: %s). Admin is reviewing it.";
    public static final String ORDER_CONFLICT_FARMER_TITLE = "Action Required: Conflict Reported!";
    public static final String ORDER_CONFLICT_FARMER_BODY = "A conflict has been reported for your order for %s (ID: %s).";
    public static final String ORDER_CONFLICT_RIDER_TITLE = "Alert: Dispute Raised!";
    public static final String ORDER_CONFLICT_RIDER_BODY = "The buyer has reported a conflict for order %s (ID: %s).";
    
    public static final String ORDER_RESOLVED_TITLE = "Conflict Resolved!";
    public static final String ORDER_RESOLVED_BODY = "Conflict for order %s (%s) has been resolved. Status updated to RESOLVED.";
    
    public static final String ORDER_CANCELLED_TITLE = "Order Cancelled!";
    public static final String ORDER_CANCELLED_BODY = "Order %s (%s) has been cancelled by Admin. Any pending stock has been restored.";

    // Stock Notifications
    public static final String STOCK_LOW_TITLE = "Low Stock Alert!";
    public static final String STOCK_LOW_BODY = "Your stock '%s' is running low (Current: %.2f). Click to restock!";

    // Demand Notifications
    public static final String DEMAND_CREATED_TITLE = "New Market Demand!";
    public static final String DEMAND_CREATED_BODY = "A new demand for %s (%.2f) has been posted. Claim it now!";
    public static final String DEMAND_ACCEPTED_BUYER_TITLE = "Demand Accepted!";
    public static final String DEMAND_ACCEPTED_BUYER_BODY = "Farmer '%s' has accepted your demand for %s. View your demand dashboard.";
    public static final String DEMAND_ACCEPTED_FARMER_TITLE = "Acceptance Confirmed";
    public static final String DEMAND_ACCEPTED_FARMER_BODY = "You've successfully accepted the demand for %s. View your stock list to fulfill.";

    // Verification Notifications
    public static final String VERIFICATION_APPROVED_TITLE = "Application Approved!";
    public static final String VERIFICATION_APPROVED_BODY = "Congratulations! Your request for %s role has been verified. You can now access your dashboard.";
    public static final String VERIFICATION_REJECTED_TITLE = "Application Rejected";
    public static final String VERIFICATION_REJECTED_BODY = "Your application for %s role was declined. Reason: %s. Register again.";

    public static final String NOTIFICATION_NOT_FOUND = "Notification not found";
}
