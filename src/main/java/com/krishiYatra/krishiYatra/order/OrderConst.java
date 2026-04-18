package com.krishiYatra.krishiYatra.order;

public class OrderConst {
    public static final String CREATE_ORDER = "Order created successfully.";
    public static final String ORDER_NOT_FOUND = "Order not found.";
    public static final String BUYER_NOT_FOUND = "Buyer profile not found for current user.";
    public static final String BUYER_NOT_VERIFIED = "Buyer is not verified.";
    public static final String STOCK_NOT_FOUND = "Stock not found.";
    public static final String STOCK_NOT_ACTIVE = "Stock is not active for ordering.";
    public static final String FARMER_NOT_VERIFIED = "Farmer is not verified.";
    public static final String MINIMUM_ORDER_QUANTITY = "Order quantity is below stock minimum quantity.";
    public static final String INSUFFICIENT_STOCK = "Insufficient stock quantity available.";
    public static final String DELIVERY_NOT_FOUND = "Delivery profile not found for current user.";
    public static final String DELIVERY_NOT_VERIFIED = "Delivery is not verified.";
    public static final String ORDER_NOT_AVAILABLE_FOR_DELIVERY = "Order is not available for delivery acceptance.";
    public static final String DELIVERY_ACCEPTED = "Delivery accepted. Order moved to shipping.";
    public static final String FETCH_ORDER = "Orders fetched successfully.";
    public static final String ORDER_DETAIL = "Order details fetched";
    public static final String ORDER_DELIVERED = "Order delivered successfully";

    public static final String OWN_STOCK_ORDER = "You cannot order your own stock.";
    public static final String ORDER_MUST_BE_ACCEPTED = "Order must be ACCEPTED before picking up.";
    public static final String ORDER_PICKED_UP = "Order picked up and is now SHIPPING.";
    public static final String UNAUTHORIZED = "Unauthorized";
    public static final String CHECKPOINTS_UPDATED = "Checkpoints updated";
    public static final String FARMER_INFO_UNAVAILABLE = "Farmer information not available.";
    public static final String FARMER_ADDRESS_NOT_SET = "Farmer has not set an address.";
    public static final String FARMER_ADDRESS_FETCHED = "Farmer address fetched.";
    public static final String ACCEPTED_ORDERS_FETCHED = "Fetched accepted orders";
    public static final String CANCEL_ALREADY_DELIVERED = "Cannot cancel an order that is already delivered or cancelled.";
    public static final String ORDER_CANCELLED = "Order cancelled successfully and stock restored.";
    public static final String CONFLICT_MSG_REQUIRED = "Conflict message is compulsory and cannot be empty.";
    public static final String CONFLICT_ONLY_DELIVERED = "Conflict can only be reported for delivered orders.";
    public static final String CONFLICT_REPORTED = "Conflict reported successfully. Admin will review it.";
    public static final String CONFLICT_ONLY_RESOLVE = "Only orders with CONFLICT status can be resolved.";
    public static final String CONFLICT_RESOLVED = "Conflict marked as RESOLVED.";
    public static final String BOUGHT_ORDERS_FETCHED = "Your bought orders fetched successfully.";
    public static final String SOLD_ORDERS_FETCHED = "Your sold orders fetched successfully.";
    public static final String DELIVERED_ORDERS_FETCHED = "Your delivered orders fetched successfully.";
}
